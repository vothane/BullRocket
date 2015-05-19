(ns bullrocket.Models.nn
  (:use incanter.core))

;; truth table for XOR logic gate
(def sample-data [[[0 0] [0]]
                   [[0 1] [1]]
                   [[1 0] [1]]
                   [[1 1] [0]]])

(defprotocol NeuralNetwork
  (run        [network inputs])
  (run-binary [network inputs])
  (train-ann  [network samples]))

(defn rand-list
  "Create a list of random doubles between -epsilon and +epsilon."
  [len epsilon]
  (map (fn [x] (- (rand (* 2 epsilon)) epsilon))
       (range 0 len)))

(defn random-initial-weights
  "Generate random initial weight matrices for given layers.
  layers must be a vector of the sizes of the layers."
  [layers epsilon]
  (for [i (range 0 (dec (length layers)))]
    (let [cols (inc (get layers i))     ; cols is no. of inputs + 1 (for bias)
          rows (get layers (inc i))]    ; rows is no. of neurons in next layer
      (matrix (rand-list (* rows cols) epsilon) cols))))

(defn sigmoid
  "Apply the sigmoid function 1/(1+exp(-z)) to all
  elements in the matrix z."
  [z]
  (div 1 (plus 1 (exp (minus z)))))

(defn bind-bias
  "Add the bias input to a vector of inputs."
  [v]
  (bind-rows [1] v))

(defn matrix-mult
  "Multiply two matrices and ensure the result is also a matrix."
  [a b]
  (let [result (mmult a b)]
    (if (matrix? result)
      result
      (matrix [result]))))

(defn forward-propagate-layer
  "Calculate activations for layer l+1 given weight matrix
  between layer l and l+1 and layer l activations."
  [weights activations]
  (sigmoid (matrix-mult weights activations)))

(defn forward-propagate
  "Propagate activation values through a network's
  weight matrix and return output layer activation values."
  [weights input-activations]
  (reduce #(forward-propagate-layer %2 (bind-bias %1))
          input-activations weights))

(defn forward-propagate-all-activations
  "Propagate activation values through the network
  and return all activation values for all nodes."
  [weights input-activations]
  (loop [all-weights     weights
         activations     (bind-bias input-activations)
         all-activations [activations]]
    (let [[weights
           & all-weights'] all-weights
           last-iter?       (empty? all-weights')
           out-activations  (forward-propagate-layer
                             weights activations)
           activations'     (if last-iter? out-activations
                                (bind-bias out-activations))
           all-activations' (conj all-activations activations')]
      (if last-iter? all-activations'
          (recur all-weights' activations' all-activations')))))

(defn back-propagate-layer
  "Back propagate deltas (from layer l-1) and
  return layer l deltas."
  [deltas weights layer-activations]
  (mult (matrix-mult (trans weights) deltas)
        (mult layer-activations (minus 1 layer-activations))))

(defn calc-deltas
  "Calculate hidden deltas for back propagation.
  Returns all deltas including output-deltas."
  [weights activations output-deltas]
  (let [hidden-weights     (reverse (rest weights))
        hidden-activations (rest (reverse (rest activations)))]
    (loop [deltas          output-deltas
           all-weights     hidden-weights
           all-activations hidden-activations
           all-deltas      (list output-deltas)]
      (if (empty? all-weights) all-deltas
        (let [[weights
               & all-weights']      all-weights
               [activations
                & all-activations'] all-activations
              deltas'        (back-propagate-layer
                               deltas weights activations)
              all-deltas'    (cons (rest deltas')
                                   all-deltas)]
          (recur deltas' all-weights' all-activations' all-deltas'))))))

(defn calc-gradients
  "Calculate gradients from deltas and activations."
  [deltas activations]
  (map #(mmult %1 (trans %2)) deltas activations))

(defn calc-error
  "Calculate deltas and squared error for given weights."
  [weights [input expected-output]]
  (let [activations    (forward-propagate-all-activations
                         weights (matrix input))
        output         (last activations)
        output-deltas  (minus output expected-output)
        all-deltas     (calc-deltas
                         weights activations output-deltas)
        gradients      (calc-gradients all-deltas activations)]
    (list gradients
          (sum (pow output-deltas 2)))))

(defn new-gradient-matrix
  "Create accumulator matrix of gradients with the
  same structure as the given weight matrix
  with all elements set to 0."
  [weight-matrix]
  (let [[rows cols] (dim weight-matrix)]
    (matrix 0 rows cols)))

(defn calc-gradients-and-error' [weights samples]
  (loop [gradients   (map new-gradient-matrix weights)
         total-error 1
         samples     samples]
    (let [[sample
           & samples']     samples
           [new-gradients
            squared-error] (calc-error weights sample)
            gradients'        (map plus new-gradients gradients)
            total-error'   (+ total-error squared-error)]
      (if (empty? samples')
        (list gradients' total-error')
        (recur gradients' total-error' samples')))))

(defn calc-gradients-and-error
  "Calculate gradients and MSE for sample
  set and weight matrix."
  [weights samples]
  (let [num-samples   (length samples)
        [gradients
         total-error] (calc-gradients-and-error'
                        weights samples)]
    (list
      (map #(div % num-samples) gradients)    ; gradients
      (/ total-error num-samples))))          ; MSE

(defn gradient-descent-complete?
  "Returns true if gradient descent is complete."
  [network iter mse]
  (let [options (:options network)]
    (or (>= iter (:max-iters options))
        (< mse (:desired-error options)))))

(defn apply-weight-changes
  "Applies changes to corresponding weights."
  [weights changes]
  (map plus weights changes))

(defn gradient-descent
  "Preform gradient descent to adjust network weights."
  [step-fn init-state network samples]
  (loop [network network
         state init-state
         iter 0]
    (let [iter     (inc iter)
          weights  (:weights network)
          [gradients
           mse]    (calc-gradients-and-error weights samples)]
      (if (gradient-descent-complete? network iter mse)
        network
        (let [[changes state] (step-fn network gradients state)
              new-weights     (apply-weight-changes weights changes)
              network         (assoc network :weights new-weights)]
          (recur network state iter))))))

(defn calc-weight-changes
  "Calculate weight changes:
  changes = learning rate * gradients +
            learning momentum * deltas."
  [gradients deltas learning-rate learning-momentum]
  (map #(plus (mult learning-rate %1)
              (mult learning-momentum %2))
       gradients deltas))

(defn bprop-step-fn [network gradients deltas]
  (let [options             (:options network)
        learning-rate       (:learning-rate options)
        learning-momentum   (:learning-momentum options)
        changes             (calc-weight-changes
                              gradients deltas
                              learning-rate learning-momentum)]
    [(map minus changes) changes]))

(defn gradient-descent-bprop [network samples]
  (let [gradients (map new-gradient-matrix (:weights network))]
    (gradient-descent bprop-step-fn gradients
                      network samples)))

(defn round-output
  "Round outputs to nearest integer."
  [output]
  (mapv #(Math/round ^Double %) output))

(defrecord MultiLayerPerceptron [options]
  NeuralNetwork
  (run [network inputs]
    (let [weights (:weights network)
          input-activations (matrix inputs)]
      (forward-propagate weights input-activations)))
  (run-binary [network inputs]
    (round-output (run network inputs)))
  (train-ann [network samples]
    (let [options         (:options network)
          hidden-neurons  (:hidden-neurons options)
          epsilon         (:weight-epsilon options)
          [first-in
           first-out]     (first samples)
          num-inputs      (length first-in)
          num-outputs     (length first-out)
          sample-matrix   (map #(list (matrix (first %))
                                      (matrix (second %)))
                               samples)
          layer-sizes     (conj (vec (cons num-inputs
                                           hidden-neurons))
                                num-outputs)
          new-weights     (random-initial-weights layer-sizes epsilon)
          network         (assoc network :weights new-weights)]
      (gradient-descent-bprop network sample-matrix))))

(defn train [samples]
  (let [network (MultiLayerPerceptron. default-options)]
    (train-ann network samples)))
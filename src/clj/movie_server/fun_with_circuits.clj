(ns movie-server.fun-with-circuits)

(defprotocol Circuit
  (tripped? [this])
  (trip! [this])
  (recover! [this])
  (error [this]))

(defrecord AtomicBooleanCircuit [state error]
  Circuit
  (tripped? [this]
    @state)
  (trip! [this]
    (reset! state true))
  (recover! [this]
    (reset! state false))
  (error [this]
    error))

(defn atomic-boolean-circuit
  [error]
  (map->AtomicBooleanCircuit {:state (atom false) :error error}))

(defn with-circuit
  [circuit trip? operation]
  (if (tripped? circuit)
    (error circuit)
    (let [output (operation)]
      (if (trip? output)
        (do (trip! circuit)
            (error circuit))
        output))))

(comment
  (def circuit (atomic-boolean-circuit {:status :error}))
  (with-circuit
    circuit
    #(= :fail (:status %))
    (fn []
      {:status :ok}))
  )

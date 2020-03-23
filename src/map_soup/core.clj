(ns map-soup.core
  (:import [org.jsoup Jsoup]
           [org.jsoup.select Elements]
           [org.jsoup.nodes Element])
  (:require [clojure.string :as str]))

(declare parse-list)
(declare parse-obj)
(declare parse-val)

(defn- extract-attr [selector]
  (let [tokens (str/split selector #" ")
        last-token (last tokens)]
    (if (str/includes? last-token "/")
      (let [[last-token attr] (str/split last-token #"/")]
        {:selector (str/join " " (-> (into [] (butlast tokens))
                                     (conj last-token)))
         :attr attr})
      {:selector selector})))

;; todo swap selector and doc positions in arg
(defn- select [doc selector]
  (.select doc selector))

;; todo swap selector and doc positions in arg
(defn- select-first [doc selector]
  (-> doc
      (select selector)
      (first)))

(defn- html->jsoup [html]
  (Jsoup/parse html))

(defn- html->clj-2
  "Takes a Jsoup document, a map of selectors and returns a map of the selections."
  [selector-map doc]
  (let [selector->clj (fn [[key value]]
                        (cond
                          (vector? value)
                          (parse-list doc selector-map key value)
                          
                          (map? value)
                          (parse-obj doc selector-map key value)
                          
                          :else
                          (parse-val doc selector-map key value)))]
    (if (:__selector selector-map)
      (html->clj-2 (dissoc selector-map :__selector) (select-first doc (:__selector selector-map)))
      (into {} (map selector->clj selector-map)))))

(defn html->clj [selector-map html]
  (html->clj-2 selector-map (html->jsoup html)))

(defn- parse-list [doc selector-map key value]
  (let [[selector-map] value
        selector       (:_selector selector-map)
        selector-map   (dissoc selector-map :_selector)
        new-docs       (select doc selector)]
    [key (into [] (map #(html->clj-2 selector-map %) new-docs))]))

(defn- parse-obj [doc selector-map key value]
  ;; the (conj {} %) is needed to turn the map entry back to a map
  [key (map #(html->clj-2 (dissoc (conj {} %)) doc) value)])

(defn- parse-val [doc selector-map key value]
  (let [{:keys [selector attr]} (extract-attr value)
        tokens                  (str/split value #" ")
        last-token              (last tokens)]
    (if-let [val (select-first doc selector)]
      (if attr
        [key (.attr val attr)]
        [key (.text val)])
      [key nil])))

(ns map-soup.core
  (:import [org.jsoup Jsoup]
           [org.jsoup.select Elements]
           [org.jsoup.nodes Element])
  (:require [clojure.string :as str]))

(defn- extract-attr [selector]
  (let [tokens (str/split selector #" ")
        last-token (last tokens)]
    (if (str/includes? last-token "/")
      (let [[last-token attr] (str/split last-token #"/")]
        {:selector (str/join " " (-> (into [] (butlast tokens))
                                     (conj last-token)))
         :attr attr})
      {:selector selector})))

(defn- select [doc selector]
  (.select doc selector))

(defn- select-first [doc selector]
  (-> doc
      (select selector)
      (first)))

(defn- html->jsoup [html]
  (Jsoup/parse html))

(defn- html->clj-2
  "Takes a Jsoup document, a map of selectors and returns a map of the selections."
  [selector-map doc]
  (into {} (map (fn [[key value]]
                  (cond
                    (vector? value)
                    (let [[selector-map] value
                          selector       (:_selector selector-map)
                          selector-map   (dissoc selector-map :_selector)
                          new-docs       (select doc selector)]
                      [key (into [] (map #(html->clj-2 selector-map %) new-docs))])
                    
                    (map? value)
                    ;; the (conj {} %) is needed to turn the map entry back to a map
                    [key (map #(html->clj-2 (conj {} %) doc) value)]

                    :else
                    (let [{:keys [selector attr]} (extract-attr value)
                          tokens     (str/split value #" ")
                          last-token (last tokens)]
                      (if-let [val (select-first doc selector)]
                        (if attr
                          [key (.attr val attr)]
                          [key (.text val)])
                        [key nil]))))
                selector-map)))

(defn html->clj [selector-map html]
  (html->clj-2 selector-map (html->jsoup html)))

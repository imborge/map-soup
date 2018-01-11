(ns map-soup.core-test
  (:require [clojure.test :refer :all]
            [map-soup.core :refer :all])
  (:import [org.jsoup Jsoup]
           [org.jsoup.select Elements]
           [org.jsoup.nodes Element]))

(def html
  "<!doctype html>
  <html> 

  <head>
  </head>
  <body>
  <div id='header'>
  <h1><a href=\"/example\">Example</a></h1>
  </div><!-- #header -->

  <div id='articles'>
  <div class='article'>
  <h2>Article 1</h2>
  <p>Some text</p>
  </div><!-- .article -->
  <div class='article'>
  <h2>Article 2</h2>
  <p>Hello from Mars</p>
  </div><!-- .article -->
  <div
  </div><!-- #articles -->
  </body>
  </html>")

(def doc (Jsoup/parse html))

(deftest map-doc-test
  (testing "A simple flat selector-map"
    (let [selector-map {:header "h1"}
          res          (map-doc selector-map doc)]
      (is (= res
             {:header "Example"}))))

  (testing "Nested selectors"
    (let [selector-map {:articles [{:_selector "div.article"
                                    :header    "h2"
                                    :content   "p"}]}
          res          (map-doc selector-map doc)]
      (is (= res
             {:articles [{:header  "Article 1"
                          :content "Some text"}
                         {:header  "Article 2"
                          :content "Hello from Mars"}]}))))

  (testing "Getting attributes"
    (let [selector-map {:url "a/href"}
          res          (map-doc selector-map doc)]
      (is (= res
             {:url "/example"})))))

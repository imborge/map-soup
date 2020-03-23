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

  <div id=\"data-test\" data-id=\"3\">3</div>
  <div id=\"data-test\" data-id=\"2\">2</div>
  </body>
  </html>")

(deftest map-doc-test
  (testing "A simple flat selector-map"
    (let [selector-map {:header "h1"}
          res          (html->clj selector-map html)]
      (is (= res
             {:header "Example"}))))

  (testing "Nested selectors"
    (let [selector-map {:articles [{:_selector "div.article"
                                    :header    "h2"
                                    :content   "p"}]}
          res          (html->clj selector-map html)]
      (is (= res
             {:articles [{:header  "Article 1"
                          :content "Some text"}
                         {:header  "Article 2"
                          :content "Hello from Mars"}]}))))

  (testing "Extracting attributes"
    (let [selector-map {:url "a/href"}
          res          (html->clj selector-map html)]
      (is (= res
             {:url "/example"}))))

  (testing "Extracting data-attributes"
    (let [selector-map {:id "div#data-test/data-id"}
          res          (html->clj selector-map html)]
      (is (= res
             {:id "3"}))))

  (testing "Finds node using attribute"
    (let [selector-map {:id "div[data-id=3]"}
          res          (html->clj selector-map html)]
      (is (= res
             {:id "3"})))))

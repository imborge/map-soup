
# map-soup

A JSoup utility library for Clojure that makes it easier to extract content
from HTML.

## Installation

map-soup is available as a Maven artifact from Clojars:

In your project.clj dependencies for leiningen:

```[borge/map-soup "0.1.0"]```

## Usage

jsoup-utils have 2 functions you are going to use: ```parse-html```
and ```parse-doc```.

```parse-html``` takes a string containing HTML and returns a JSoup document.

```map-doc``` takes a JSoup document and a map of selectors, and returns 
a copy of the selector-map where the  values are extracted from the document using the selectors.

### Quick example

```clojure
(def doc (parse-html "..."))

(def selector-map 
  {:header "h1"
   :articles
   [{:_selector ".article"
     :title "h2"
     :url   "a/href"
     :body  "p"}]})

(map-doc selector-map doc)
;; => {:header "Example"
;;     :articles
;;     [{:title "Article 1"
;;       :url   "/article-1"
;;       :body  "Some text"}
;;      {:title "Article 2"
;;       :url   "/article-2"
;;       :body  "Hello from Mars"}]}
```

### Full example

```clojure
(ns example.core
  (:require [map-soup.core :refer [map-doc parse-html]]))

(def html
  "<!doctype html>
  <html> 

  <head>
  </head>
  <body>
  <div id='header'>
  <h1><a href='/example'>Example</a></h1>
  </div><!-- #header -->

  <div id='articles'>
  <div class='article'>
  <h2><a href='/article-1'>Article 1</a></h2>
  <p>Some text</p>
  </div><!-- .article -->
  <div class='article'>
  <h2><a href='/article-2'>Article 2</a></h2>
  <p>Hello from Mars</p>
  </div><!-- .article -->
  <div
  </div><!-- #articles -->
  </body>
  </html>")

(def selector-map
  {:header "h1"
   ;; Extract lists:
   :articles
   ;; :_selector is needed, and must select the elements that
   ;; there are many of, eg "ul li", not just the "ul".
   [{:_selector ".article" ;; we have many ".article"'s
     :title "h2"
     ;; extract attributes using "/":
     :url   "a/href"
     :body  "p"}]})

(def doc (parse-html html))

(map-doc selector-map doc)
;; => {:header "Example"
;;     :articles
;;     [{:title "Article 1"
;;       :url   "/article-1"
;;       :body  "Some text"}
;;      {:title "Article 2"
;;       :url   "/article-2"
;;       :body  "Hello from Mars"}]}
```

## License

Copyright © 2018 Børge André Jensen

Distributed under the [MIT License](http://opensource.org/licenses/MIT)

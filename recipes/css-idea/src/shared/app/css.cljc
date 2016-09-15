(ns app.css
  #?(:clj
     (:use com.rpl.specter))
  (:require [clojure.string :as str]
            [garden.core :as g]
            [om.next :as om]))

#?(:cljs
   (defprotocol CSS
     (css [this] "Specifies the component-local CSS")))

#?(:cljs
   (defn cssify
     "Replaces slashes and dots with underscore."
     [str] (str/replace str #"[./]" "_")))

#?(:cljs
   (defn local-kw
     "returns a keyword for a localized CSS class"
     ([comp-class]
      (keyword (str "." (cssify (pr-str comp-class)))))
     ([comp-class nm]
      (keyword (str "." (cssify (pr-str comp-class)) "__" (name nm))))))

#?(:cljs
   (defn local-class
     "returns a string name of a localized CSS class"
     ([comp-class]
      (str (cssify (pr-str comp-class))))
     ([comp-class nm]
      (str (cssify (pr-str comp-class)) "__" (name nm)))))

#?(:cljs
   (defn css-merge
     "Merge together the CSS of components and other literal CSS maps (items can be maps or Components that implement CSS"
     [& items]
     (reduce
       (fn [acc i]
         (cond
           (implements? CSS i) (let [rules (css i)]
                                 (if (every? vector? rules)
                                   (into acc rules)
                                   (conj acc rules)))
           (vector? i) (conj acc i)
           :else acc)) [] items)))

#?(:cljs
   (defn remove-css "Remove the CSS style by id"
     [id]
     (if-let [old-element (.getElementById js/document id)]
       (let [parent (.-parentNode old-element)]
         (.removeChild parent old-element)))))

#?(:cljs
   (defn upsert-css [id root-component]
     (remove-css id)
     (let [style-ele (.createElement js/document "style")]
       (set! (.-innerHTML style-ele) (g/css (css root-component)))
       (.setAttribute style-ele "id" id)
       (.appendChild (.-body js/document) style-ele))))

#?(:clj
   (defn defines-class? [ele] (and (map? ele) (contains? ele :class))))

#?(:clj
   (defn localize-classnames
     "Replace any class names in map m with localized versions (names prefixed with $ will be mapped to root)"
     [class m]
     (let [subclass (:class m)
           entry (fn [c]
                   (let [cn (name c)]
                     (if (str/starts-with? cn "$")
                       (str/replace cn #"^[$]" "")
                       `(app.css/local-class ~class ~cn))))
           subclasses (if (vector? subclass)
                        (apply list (reduce (fn [acc c] (conj acc (entry c))) ['str] subclass))
                        (entry subclass))]
       (-> m
           (assoc :className subclasses)
           (dissoc :class)))))

#?(:clj
   (defmacro apply-css [class body]
     (transform (walker defines-class?) (partial localize-classnames class) body)))

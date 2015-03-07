(ns mentionmyfollowers.views
    (:require [net.cgrand.enlive-html :as html]))

(html/defsnippet instagram-logout-script "templates/confirmation-page.html" [[:script html/last-of-type]] [])

(html/deftemplate 
    index 
    "public/index.html"
    [title-text button-text]
    [:header#top :h3] (html/content title-text)
    [:header#top :a] (html/content button-text)
    [:body] (html/append (instagram-logout-script)))

(defmulti error-type (fn [error] error) :default "unknown_error")
(defmethod error-type "access_denied" [_] :access-denied)
(defmethod error-type "unknown_error" [_] :unknown-error)

(defmulti view-type (fn [result-type & view-params] result-type))
(defmethod view-type :success [& _] :success)
(defmethod view-type :error [_ error & _] 
    (println "Getting dispatch-value for error" error)
    (let [the-type (error-type error)]
        (println "...got error type for" error the-type)
        the-type))

(defmethod view-type :unknown-request [& _] :unknown-request)

(defmulti registration-view view-type)
(defmethod registration-view :success [result-type code] 
    (index "You successfully registered with application. Do you want to register another user?" 
           "Register another user"))

(defmethod registration-view :access-denied [result-type error reason description]
    (index
        (str "You rejected the application. Do you want to try again?")
        "Try again"))

(defmethod registration-view :unknown-error [result-type error reason description]
    (index
        (str "An unkonwn error for user registration: " error "/" reason "/" description " Do you want to try again?")
        "Try again"))

(defmethod registration-view :unknown-request [_ request-params]
    (index
        (str "An unknown request received from Instagram " request-params ". Do you want to try again?")
        "Try again"))


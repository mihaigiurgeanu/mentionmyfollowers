(ns mentionmyfollowers.instagram
    (:require [org.httpkit.client :as http]))

(defn get [url options]
    (http/get url (assoc-in options [:query-params :client_id] "b90fd0e7168c45c3a72f41ba2214a66e")))
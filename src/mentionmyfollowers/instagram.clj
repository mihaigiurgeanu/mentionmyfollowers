(ns mentionmyfollowers.instagram
    (:require [org.httpkit.client :as http]))

(defn get [url options]
    (http/get url (assoc-in options [:query-params :client_id] "df3b36309ba2422bac3198d5a6f7cc72")))
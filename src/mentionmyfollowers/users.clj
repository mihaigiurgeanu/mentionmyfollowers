(ns mentionmyfollowers.users
    (:require [clojure.edn :as edn]
              [clojure.java.io :as io]
              [clj-json.core :as json]
              [org.httpkit.client :as http]
              [mentionmyfollowers.instagram :as instagram]))

(def ^:dynamic *users-file-name* "users.edn")
(defn- read-users
    "Reads the users form the *users-file-name*. The file is searched
    in the class path."
    []
    (with-open [in (java.io.PushbackReader. (io/reader *users-file-name*))]
        (edn/read in)))

(def ^:private ^:dynamic *users* (agent (read-users)))

(defn- add-user! [users json-user]
    (binding [*out* *err*]
        (println "Saving new json-user info: " json-user)
        (let [user (json/parse-string json-user true)]
            (if-let [user-id (-> user :user :id)]
                (let [users_ (assoc users user-id user)]
                    (with-open [out (io/writer *users-file-name*)]
                        (binding [*out* out]
                            (pr users_)))
                    users_)
                (do (println "User info not received" 
                             (:code json-user) 
                             (:error_type json-user) 
                             (:error_message json-user))
                    users)))))

(defn save-user-info! [code] 
    (http/post
        "https://api.instagram.com/oauth/access_token"
        {:form-params {"client_id" "b90fd0e7168c45c3a72f41ba2214a66e"
                       "client_secret" "6fac06a426394034ab839bcc803a4d8f"
                       "grant_type" "authorization_code"
                       "redirect_uri" "http://dragon-alien.codio.io:3000/signup-result"
                       "code" code}}
        (fn [{:keys [status headers error body]}]
            (if error 
                (binding [*out* *err*]
                    (println "Could not get the authentication-token for code" code)
                    (println error))
                (send *users* add-user! body)))))

(defn- check-and-throw-http-error! [error status]
    (when error (throw (ex-info
                           "Error calling instagram api."
                           {:error_type "HTTPEndpointError"
                            :code 400
                            :error_message (str "Instagram HTTP request with status " status " and error " error)}))))

(defn- collect-data [body collect-fn]
    (if body
        (let [{response-meta :meta data :data {next-url :next_url} :pagination} (json/parse-string body true)]
            (if (not= 200 (:code response-meta))
                (binding [*out* *err*] (println "Error returned by instagram api call" response-meta))
                {:data (collect-fn data)
                 :next-url next-url}))
        {:data (collect-fn []) :next-url nil}))

(defn search-users [access-token user-name]
    (binding [*out* *err*]
        (println "Sending search users query to instagram" user-name access-token))
    (let [search-request (instagram/get
                             "https://api.instagram.com/v1/users/search"
                             {:query-params {:q user-name 
                                             :access_token access-token}})]

        (loop [{:keys [body error status]} @search-request received-ids []]
            (check-and-throw-http-error! error status)
            (let [{:keys [data next-url]} 
                  (collect-data body (fn [users]
                                         (binding [*out* *err*]
                                             (doall (map #(println "Received user:" (:id %) (:username %) (:full_name %)) users)))
                                         (concat received-ids (map :id (filter #(= user-name (:username %)) users)))))]
                (if next-url
                    (recur (http/get next-url) data)
                    data)))))

(defn search-followers [access-token user-id]
    (binding [*out* *err*]
        (println "Sending followed-by query to instagram" user-id access-token))
    (let [search-request (instagram/get
                             (str "https://api.instagram.com/v1/users/" user-id "/followed-by")
                             {:query-params {:access_token access-token}})]
        (loop [{:keys [body error status]} @search-request 
               received-followers []]
            (check-and-throw-http-error! error status)
            (let [{:keys [data next-url]}
                  (collect-data body #(concat received-followers (map :username %)))]
                (if next-url
                    (recur (http/get next-url) data)
                    data)))))

(defn access-token [user-id] 
    (:access_token (@*users* user-id)))

(defn user-info [user-id]
    (:user (@*users* user-id)))


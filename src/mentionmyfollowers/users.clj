(ns mentionmyfollowers.users
    (:require [clojure.edn :as edn]
              [clojure.java.io :as io]
              [org.httpkit.client :as http]
              [clj-json.core :as json]))

(def ^:dynamic *users-file-name* "users.edn")

(defn- read-users
    "Reads the users form the *users-file-name*. The file is searched
    in the class path."
    []
    (with-open [in (java.io.PushbackReader. (io/reader (io/resource *users-file-name*)))]
        (edn/read in)))

(def ^:private ^:dynamic *users* (agent (read-users)))

(defn- add-user! [users user]
    (let [users_ (assoc users (-> user :user :id) user)]
        (with-open [out (io/writer (io/resource *users-file-name*))]
            (binding [*out* out]
                (pr users_)))
        users_))

(defn save-user-info! [code] 
    (http/post
        "https://api.instagram.com/oauth/access_token"
        {:form-params {"client_id" "b90fd0e7168c45c3a72f41ba2214a66e"
                       "client_secret" "6fac06a426394034ab839bcc803a4d8f"
                       "grant_type" "authorization_code"
                       "redirect_uri" "http://dragon-alien.codio.io:3000/signup-result"
                       "code" code}}
        (fn [{:keys {status headers error body}}]
            (if error 
                (binding [*out* *err*]
                    (println "Could not get the authentication-token for code" code)
                    (println error))
                (let [user (json/pase-string body true)]
                    (send *users* add-user! user))))))

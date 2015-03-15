(ns mentionmyfollowers.core
    (:use compojure.core
          ring.middleware.defaults
          mentionmyfollowers.views
          mentionmyfollowers.users)
    (:require [compojure.route :as route]
              [ring.util.response :as response]
              [ring.middleware.format :refer [wrap-restful-format]]))

(defn handle-exception-info
    "Ring midleware that chatches IExceptionInfo instances creates a 500
    response sending exception info as edn data."
    [handler]
    (fn [request]
        (try 
            (handler request)
            (catch clojure.lang.ExceptionInfo e 
                (.printStackTrace e)
                {:status 500
                 :body (.getData e)}))))

(defn- log-response [handler]
    "Ring middleware that logs the request and response on stderr."
    (fn [{:keys [uri query-string] :as req}]
        (let [response (handler req)]
            (binding [*out* *err*]
                (println "Sending response for req" uri query-string)
                (println response))
            response)))

(defn- set-access-token-param 
    "Ring middleware that retrieves the stored instagram access_token
    and sets :access-token key in :params map. If the user-id is missing
    or the access_token is not available, the middleware throws exception."
    [handler]
    (fn [{{user-id :user-id} :params :as req}]
        (let [token (access-token user-id)]
            (if token
                (handler (assoc-in req [:params :access-token] token))
                (throw (ex-info "Access token is missing. The user must register app in his/her instagram account."
                                {:error_type "MissingAccessToken"
                                 :code 400
                                 :error_message (str "Missing access_token for user id " user-id)}))))))

(defn- replace-user-names [handler]
    "Ring middleware that checks the :params map from request if it has
    a :user-ids key and a :user-names key. If the :user-names is present and
    has a non-nil value and the :user-ids is not present or has a nil value
    then it will query instagram about the users in the :user-names vector and
    will set :user-ids with a vector with corresponding ids."
    (fn [{{:keys [user-names user-ids access-token]} :params :as req}]
        (handler 
            (if (and user-names (not user-ids))
                (let [ids (mapcat #(search-users access-token %) user-names)]
                    (binding [*out* *err*] (println "Setting user ids:" ids))
                    (assoc-in req [:params :user-ids] ids))
                req))))

(defroutes route-api
    (GET "/api/users/:userid" [userid] 
         (let [the-info (user-info userid)]
             (binding [*out* *err*]
                 (println "Returning user info for userId:" userid the-info))
             {:body the-info}))
    (set-access-token-param
        (replace-user-names
            (POST "/api/followers" [access-token user-ids] (mapcat #(search-followers access-token %) user-ids)))))

(defroutes route-views
    (GET "/" [] (response/redirect "/index.html"))
    (GET "/signup-result" [code] 
         (when code 
             (save-user-info! code)
             (apply str (registration-view :success code))))
    (GET "/signup-result" [error error_reason error_description] 
         (when error (apply str (registration-view :error error error_reason error_description))))
    (GET "/signup-result" {params :params} (apply str (registration-view :unknown-request params)))
    (route/resources "/"))

(defroutes web-app
    (-> route-api
        handle-exception-info
        wrap-restful-format
        log-response
        (wrap-defaults api-defaults))
    (wrap-defaults route-views site-defaults)
    (route/not-found (str "Service or page not found")))

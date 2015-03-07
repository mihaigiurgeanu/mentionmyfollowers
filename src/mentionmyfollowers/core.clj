(ns mentionmyfollowers.core
    (:use compojure.core
          ring.middleware.defaults
          mentionmyfollowers.views
          mentionmyfollowers.users)
    (:require [compojure.route :as route]
              [ring.util.response :as response]))

(defroutes route-api
    (GET "/api/users" [] (str "request to return the registered users"))
    (GET "/api/users/:user" [user] (str "request to get info about specific user: " user))
    (GET "/api/followers/:user" [user] (str "request followers for " user))
    (GET "/api/followers" [] (str "request to get all followers"))
    (GET "/api/addUserCode" req (str "Unkonwn session request params: " (:params req))))

(defroutes route-views
    (GET "/" [] (response/redirect "/index.html"))
    (GET "/signup-result" [code] 
         (when code 
             (save-user-info! code)
             (registration-view :success code)))
    (GET "/signup-result" [error error_reason error_description] 
         (when error (registration-view :error error error_reason error_description)))
    (GET "/signup-result" {params :params} (registration-view :unknown-request params))
    (route/resources "/"))

(defroutes web-app
    (wrap-defaults route-api api-defaults) 
    (wrap-defaults route-views site-defaults)
    (route/not-found (str "Service or page not found")))

(ns mentionmyfollowers.core
    (:use compojure.core
          ring.middleware.defaults)
    (:require [compojure.route :as route]))

(defroutes api-routes
    (GET "/api/followers" [u p] (str "request followers for " u " with pass " p))
    (GET "/session" [session code] 
         (when code (str "Received code " code " for session " session "\n")))
    (GET "/session" [session error error_reason error_description] 
         (when error (str "Received error " error "/" error_reason "/" error_description " for session " session "\n")))
    (GET "/session" req (str "Unkonwn session request params: " (:params req)))
    (route/not-found "Service not found"))

(def api (wrap-defaults api-routes api-defaults))

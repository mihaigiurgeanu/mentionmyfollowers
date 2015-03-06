(ns mentionmyfollowers.core
    (:use compojure.core
          ring.middleware.defaults)
    (:require [compojure.route :as route]))

(defroutes api-routes
    (GET "/api/followers" [u p] (str "request followers for " u " with pass " p))
    (GET "/session" [code session] (str "Received code " code " for session " session))
    (route/not-found "Service not found"))

(def api (wrap-defaults api-routes api-defaults))

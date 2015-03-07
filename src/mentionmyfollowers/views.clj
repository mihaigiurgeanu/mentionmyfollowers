(ns mentionmyfollowers.views)

(defmulti error-type (fn [error] error :default "unknown_error"))
(defmethod error-type "access_denied" [_] :access-denied)
(defmethod error-type "unknown_error" [_] :unknown-error)

(defmulti view-type (fn [result-type & view-params] result-type))
(defmethod view-type :success [& _] :success)
(defmethod view-type :error [_ error & _] (error-type error))
(defmethod view-type :unkown-request [& _] :unkown-request)

(defmulti registration-view view-type)
(defmethod registration-view :succes [result-type code] (str "User code: " code))
(defmethod registration-view :access-denied [result-type error reason description]
    (str "User rejected the application: " reason "/" description))
(defmethod registration-view :unknown-error [result-type error reason description]
    (str "An unkonwn error for user registration: " error "/" reason "/" description))
(defmethod registration-view :unkonwn-request [request-params]
    (str "An unknown request received for user registration " request-params))


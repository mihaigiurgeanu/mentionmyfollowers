(defproject mentionmyfollowers "0.1.0-SNAPSHOT"
    :description "FIXME: write description"
    :url "http://example.com/FIXME"
    :license {:name "Eclipse Public License"
              :url "http://www.eclipse.org/legal/epl-v10.html"}
    :dependencies [[org.clojure/clojure "1.6.0"]
                   [compojure "1.3.2"]
                   [ring/ring-defaults "0.1.4"]
                   [enlive "1.1.5"]
                   [clj-json "0.5.3"]
                   [http-kit "2.1.16"]
                   [ring-middleware-format "0.4.0"]]
    :plugins [[lein-ring "0.9.2"]]
    :ring {:handler mentionmyfollowers.core/web-app}
    :target-path "target/%s")

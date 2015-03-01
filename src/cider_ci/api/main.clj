; Copyright (C) 2013, 2014, 2015 Dr. Thomas Schank  (DrTom@schank.ch, Thomas.Schank@algocon.ch)
; Licensed under the terms of the GNU Affero General Public License v3.
; See the "LICENSE.txt" file provided with this software.


(ns cider-ci.api.main
  (:gen-class)
  (:require 
    [cider-ci.api.resources :as resources]
    [cider-ci.api.web :as web]
    [cider-ci.auth.core :as auth]
    [cider-ci.utils.config :as config :refer [get-config]]
    [cider-ci.utils.debug :as debug]
    [cider-ci.utils.http :as http]
    [cider-ci.utils.map :refer [deep-merge]]
    [cider-ci.utils.messaging :as messaging]
    [cider-ci.utils.nrepl :as nrepl]
    [cider-ci.utils.rdbms :as rdbms]
    [cider-ci.utils.with :as with]
    [clojure.tools.logging :as logging]
    )
  (:import 
    [org.jruby.embed InvokeFailedException ScriptingContainer]
    ))


(defn get-db-spec []
  (let [conf (get-config)]
    (deep-merge 
      (or (-> conf :database ) {} )
      (or (-> conf :services :api :database ) {} ))))

(defn -main [& args]
  (with/logging 
    (config/initialize ["../config/config_default.yml" "./config/config_default.yml" "./config/config.yml"])
    (rdbms/initialize (get-db-spec))
    (messaging/initialize (:messaging (get-config)))
    (nrepl/initialize (-> (get-config) :services :api :nrepl))
    (auth/initialize (select-keys (get-config) [:secret :session :basic_auth]))
    (web/initialize) 
    nil))


;### Debug ####################################################################
;(logging-config/set-logger! :level :debug)
;(logging-config/set-logger! :level :info)
;(debug/debug-ns *ns*)

(ns middleware.content-negotiation
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [middleware.content-negotiation :as cn]))

;(deftest get-formatter-test
;  (let [handled-formats {"application/json" json/write-str}]
;    (testing "Get Formatter"
;      (testing "with existing formatters"
;        (is (= {"application/json" json/write-str}
;               (cn/get-formatter handled-formats ["application/json"])))))))

(deftest should-conform-to-specs
  (testing "Common logic should conform to its specs"
    (is
      (let [{:keys [total check-passed]}
            (-> 'middleware.content-negotiation
                stest/enumerate-namespace
                stest/check
                stest/summarize-results)]
        (= total check-passed)))))

(ns app.pagination-spec
  (:require
    [app.core :refer [next-page prior-page]]
    [untangled-spec.core :refer-macros [specification assertions behavior component]]))

(specification "Pagination"
  (let [cache {:list-cache [{:item/id 1} {:item/id 2} {:item/id 3} {:item/id 4} {:item/id 5}
                            {:item/id 6} {:item/id 7} {:item/id 8} {:item/id 9} {:item/id 10}
                            {:item/id 11} {:item/id 12} {:item/id 13} {:item/id 14} {:item/id 15}
                            {:item/id 16} {:item/id 17} {:item/id 18} {:item/id 19} {:item/id 20}]}
        first-page {:current-page {:start         0
                                   :total-results 55
                                   :page-size     10
                                   :items         []}}
        middle-page {:current-page {:start         10
                                :total-results 55
                                :page-size     10
                                :items         []}}
        first-page-state (merge first-page cache)
        middle-page-state (merge middle-page cache)
        last-page-state (assoc-in middle-page-state [:current-page :start] 50)]
    (component "Next page"
      (assertions
        "Populates items (if available)"
        (-> first-page-state next-page :current-page :items count) => 10
        (-> first-page-state next-page :current-page :items (get 0)) => {:item/id 11}
        (-> middle-page-state next-page :current-page :items count) => 0

        "Adds the current page size to the start"
        (get-in (next-page middle-page) [:current-page :start]) => 20

        "Prevents start from exceeding the last page"
        (get-in (-> middle-page next-page next-page next-page next-page next-page) [:current-page :start]) => 45))
    (component "Prior page"
      (assertions
        "Populates items (if available)"
        (-> first-page-state prior-page :current-page :items count) => 10
        (-> first-page-state prior-page :current-page :items (get 0)) => {:item/id 1}
        (-> last-page-state prior-page :current-page :items count) => 0

        "Subtracts the current page size from the start"
        (get-in (prior-page middle-page) [:current-page :start]) => 0

        "Prevents start from going negative"
        (get-in (-> middle-page prior-page prior-page prior-page) [:current-page :start]) => 0))))


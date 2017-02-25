(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.ui.elements :as e]
            [untangled.ui.forms :as f]
            [untangled.ui.layout :as layout]
            [untangled.client.core :refer [InitialAppState initial-state]]
            [untangled.client.mutations :as m :refer [defmutation]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as df]))



(defn wizard-ident
  "Generates a wizard ident given an ID you want to use for the wizard."
  [id] [:wizard/by-id id])

(defn update-wizard
  "Runs an update on a wizard with the given ID in the graph database. `f` is a function
  from wizard -> wizard."
  [state-map wiz-id f] (update-in state-map (wizard-ident wiz-id) f))

(defn next-slide-impl
  "Given a wizard, returns a wizard with the page incremented."
  [wizard] (update wizard :wizard/step inc))

(defn prior-slide-impl
  "Given a wizard, returns a wizard with the page decremented."
  [wizard] (update wizard :wizard/step dec))

(defmutation next-slide
  "Om Mutation: Move the wizard with the given ID to the next slide."
  [{:keys [wizard-id]}]
  (action [{:keys [state]}]
    ; here we're composing together update-wizard with wizard-ops. This allows local reasoning about
    ; app state changes that are not so bit-twiddly.
    (swap! state update-wizard wizard-id next-slide-impl)))

(defmutation prior-slide
  "Om Mutation: Move the wizard with the given ID to the next slide."
  [{:keys [wizard-id]}]
  (action [{:keys [state]}]
    ; here we're composing together update-wizard with wizard-ops. This allows local reasoning about
    ; app state changes that are not so bit-twiddly.
    (swap! state update-wizard wizard-id prior-slide-impl)))

; Our "slides" are just functions that can show the part of the form that should be on a given
; wizard slide.
(defn intro-slide []
  (dom/div #js {}
    "In this short survey we are trying to determine a relation between
    how appealing women find facial hair and men's willingness to shave."))

(defn gender-slide [component form]
  (dom/div #js {}
    "Do you identify as Male of Female?"
    (dom/div nil
      (f/form-field component form :gender :choice :male :label "Male")
      (f/form-field component form :gender :choice :female :label "Female"))))

(defn shaving-slide [component form]
  (dom/div #js {}
    "Do you like to be clean-shaven?"
    (dom/div nil
      (f/form-field component form :like_shaving :choice true :label "As a baby's bottom!")
      (f/form-field component form :like_shaving :choice false :label "I'm Grizzly Adams"))))

(defn beard-slide [component form]
  (dom/div #js {}
    "Are beards sexy?"
    (dom/div nil
      (f/form-field component form :beards_sexy :choice true :label "Oh yeah!")
      (f/form-field component form :beards_sexy :choice false :label "Ugh :("))))

; The final survey results UI component
(defui ^:once Results
  static om/IQuery
  (query [this] [:men :women :like_shaving :beards_sexy])
  Object
  (render [this]
    (let [{:keys [men women like_shaving beards_sexy]} (om/props this)]
      (e/ui-card {:title "Survey Results"}
        (dom/p nil (str "Male responses: " men))
        (dom/p nil (str "Female responses: " women))
        (dom/p nil (str "Men that don't mind shaving: " like_shaving))
        (dom/p nil (str "Women who like beards: " beards_sexy))))))

(def ui-results (om/factory Results))

; This could have also been a direct call to df/load, but we chose to use a single UI transaction
; for a little more clarity in the UI.
(defmutation load-results
  "Om mutation: Load the survey results."
  [no-args]
  (action [{:keys [state]}] (df/load-action state :final-results Results))
  (remote [env] (df/remote-load env)))

(defui ^:once Wizard
  ; see forms support. Basically, declare the fields that are involved in the form,
  ; and their semantic rendering.
  static f/IForm
  (form-spec [this] [(f/id-field :id)
                     (f/radio-input :gender #{:male :female})
                     (f/radio-input :like_shaving #{true false})
                     (f/radio-input :beards_sexy #{true false})])
  ; forms support requires that you form stuff to your component's state using `build-form`
  static InitialAppState
  (initial-state [cls params] (f/build-form Wizard {:id           (om/tempid)
                                                    :gender       nil
                                                    :like_shaving nil
                                                    :beards_sexy  nil
                                                    :wizard/steps 3
                                                    :wizard/step  1}))
  static om/IQuery
  (query [this] [:id :gender :like_shaving :beards_sexy :wizard/step :wizard/steps
                 ; forms support needs you to query these special keys to ensure form data is pulled in
                 f/form-root-key f/form-key])
  static om/Ident
  (ident [this props] (wizard-ident (:id props)))
  Object
  ; the controls light up or disappear depending on the step and state of the wizard...
  (slide-controls [this]
    (let [{:keys [id gender like_shaving beards_sexy
                  wizard/steps wizard/step] :as form-props} (om/props this)
          step-incomplete? (case step
                             2 (= ::f/none gender)
                             3 (or (and (= gender :male) (= ::f/none like_shaving))
                                 (and (= gender :female) (= ::f/none beards_sexy)))
                             false)
          is-last?         (= step steps)
          is-first?        (= step 1)]
      (dom/div nil
        (e/ui-button {:onClick   #(om/transact! this `[(prior-slide {:wizard-id ~id})])
                      :className (str "c-button " (if is-first? "u-fade-out" "u-fade-in"))} "Back")
        (if is-last?
          (e/ui-button {:onClick   #(om/transact! this `[(f/commit-to-entity {:form ~form-props :remote true})
                                                         (load-results {})])
                        :disabled  step-incomplete?
                        :className (str "c-button " (if (not is-last?) "u-fade-out" "u-fade-in"))} "Done!")
          (e/ui-button {:onClick   #(om/transact! this `[(next-slide {:wizard-id ~id})])
                        :disabled  step-incomplete?
                        :className (str "c-button")} "Next")))))
  (render [this]
    (let [{:keys [id gender like_shaving beards_sexy
                  wizard/steps wizard/step] :as form-props} (om/props this)
          male? (= gender :male)]
      ; the wizard just tracks the step and shows the right slide.
      (e/ui-card {:title (str "Shaving Hot or Not    " step "/" steps)}
        (case step
          1 (intro-slide)
          2 (gender-slide this form-props)
          3 (if male?
              (shaving-slide this form-props)
              (beard-slide this form-props)))
        (.slide-controls this)))))

(def ui-wizard (om/factory Wizard))

(defui ^:once Root
  static InitialAppState
  (initial-state [cls params] {:wizard (uc/get-initial-state Wizard {})})
  static om/IQuery
  (query [this] [:ui/react-key {:wizard (om/get-query Wizard)}
                 {:final-results (om/get-query Results)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key wizard final-results]} (om/props this)]
      (layout/row {:key react-key}
        (layout/col {:width 6 :push 3}
          ; when there are results show them (indicates post-submission)
          ; otherwise we're still working on the wizard.
          (if (seq final-results)
            (ui-results final-results)
            (ui-wizard wizard)))))))

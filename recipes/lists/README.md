# Managing Lists

Different frameworks have different ways of deal with collections of
things, and mechanisms for keeping the UI up to date.  With 
Untangled the UI refresh story is reactive on two things: the 
component doing a transaction, and the co-located queries/idents.

Many beginners have trouble understanding how to get lists to work
correctly because of misunderstanding these fundamentals, which can
be summarized as:

A component (subtree) will re-render when:

- It is the initiator of a transaction
- A transaction mentions one of the things it queried in a follow-on read
- Has the same ident as a component initiating a transaction

## Follow-on Reads

A follow-on read is a keyword (property name) the follows a mutation
in a transaction. For example:

```
   (transact! this '[(a/do-somthing) :prop])
```

This is *not* a top-level read. Om keeps an index of all components by
keywords that were queried. Thus, any component anywhere in the (visible)
UI tree that queried `:prop` can be found (and therefore re-rendered in
a targeted fashion).

## Follow-on Reads vs. Callbacks

Callbacks have nothing directly to do with rendering, but when you
read the list of re-rendering triggers you realize you have two ways
to keep a list up-to-date when there are membership changes: Run
the transaction in the parent, or put a parent query follow-on read on
a child transaction:

```
(defui Child
   ...  :onClick #(transact! this '[(a/f) :parent-prop]) ...)
```

or

```
(defui Parent
   ...  :onClick #(transact! this '[(a/f)]) ...)
```

The former does an unfortunate thing: it breaks component reasoning. 
You have to think about the parent in order to write the child. This
couples that child to a specific parent, and harms code reuse and 
local reasoning (the parent code mentions the child, but not vice-versa).

The latter suffers from a different problem: What if the action you
want to do (e.g. delete) should be triggered from the child? The answer
is quite simple: callbacks.

Just as generic HTML components (such as button) have no coupling to their
parent, nor should yours. Instead write your components to "generate
transaction triggers" via callbacks. Conceptually, you want to call
the child like this:

```
(ui-list-item { :on-delete #(transact! this '[...]) })
```

The only catch is that Om wants you to pass the query results this way.

Therefore, the formal syntax uses a helper function to attach callbacks
to the query result props:

```
(ui-list-item (om/computed props { :on-delete #(transact! this '[...]) }))
```

The child, similarly, pulls the computed elements out with `get-computed`
against `props`.


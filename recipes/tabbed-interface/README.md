# Tabbed Interfaces

This recipe walks you through what you need to know in order to implement
tabbed interfaces using union queries. These techniques can be combined
with HTML5 routing to handle showing a particular form of the UI at a 
specific URL.

## Running the code

You want to run both a figwheel build and a server. See the top-level
README for instructions. Be sure you access the app through the server
port or the remote load will not work.

## Union Queries

This recipe requires union queries. Union queries have the following 
requirements:

- There must be a `defui` component that is dedicated to just the union
portion of the query. See `TabUnion` in `ui.cljs`
    - The union component must have an Ident (which gives the idents for the sub-components instead of itself)
    - A union query is a map. The keys are the "type" of the query, and the values are queries from subcomponents.
- The first element of an ident in your app state is what chooses the query
from the union component's union query. When that keyword is found in the union
map, that part of the query is used. See intial state in `core.cljs`.
- The mutations must be done in the parent. See `Root` in `ui.cljs`.
- The mutation itself is trivial. See `mutations.cljs`

From there, the subcomponents that you want to switch in/out of the UI
are as normal. See the recipe source code, which contains comments 
about each of these items.

## Lazy loading data on a screen that appears

Remember that the app state does not change unless an action changes it. So,
your navigation mutation may look at app state to see if there is a need for
data and use that to specify a remote load via the `:remote` section of the
return map. See `mutations.cljs`.

The code is very brief, but it is the shape of what to do. We define a method that can examine the
state to decide if we want to trigger a load. Then we define a mutation
that the UI can call during transact (see the `transact!` call for `Settings` on `Root` in `ui.cljs`).

The mutation itself (`app/lazy-load-tab`) uses a data-fetch helper function to
set :remote to the right thing, and can then give one or more load-data-action's to
indicate what should actually be retrieved. The server implementation is trivial in
this case. See `api.clj`.

Some notes:

The client mutation has to change the ident...there is no problem with that...optimistic update (to switch to the tab
immediately) is fine. From within *that* mutation (or as an additional mutation done at the transact level),
one can trigger a load in the mutation code. 

You want this because the UI should not go examining global state...you want an abstract mutation to do the work 
if it is coming from the UI.

The two are separated into two mutations in the code when in fact they could be combined, which is the point. 
They are separate in the example so you can see the simplicity of the tab change (one line).

If you always wanted to load something on entering the tab, you can just use `load-data` in the UI event handler; 
however, if you want to examing app state to decide on the load then you want that to happen inside of an 
abstract mutation (which has access to the complete state). It is OK if the answer is "nothing".

Also note that returning `(df/remote-read)` for `:remote` is harmless if you don't actually queue loads in the action.
We elide both in the example code, but the networking layer is smart enough to see there is nothing to do.

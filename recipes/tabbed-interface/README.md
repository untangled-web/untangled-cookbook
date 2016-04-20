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
return map. Details of how to do this are in `mutations.cljs` as comments
around code that does this for the Settings tab.



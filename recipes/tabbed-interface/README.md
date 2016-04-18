# Tabbed Interfaces

This recipe walks you through what you need to know in order to implement
tabbed interfaces using union queries. These techniques can be combined
with HTML5 routing to handle showing a particular form of the UI at a 
specific URL.

## Union Queries

This recipe requires union queries. Union queries have the following 
requirements:

- There must be a `defui` component that is dedicated to just the union
portion of the query
- The union component must have an Ident (which gives the idents for the
sub-components instead of itself)
- A union query is a map. The keys are the "type" of the query, and the values
are queries from subcomponents.
- The first element of the ident in your app state is what chooses the query
from the union component's union query. When that keyword is found in the unoin
map, that part of the query is used.
- The mutations must be in the parent. 

From there, the subcomponents that you want to switch in/out of the UI
are as normal. See the recipe source code, which contains comments 
about each of these items.

## Lazy loading data on a screen that appears

Remember that the app state does not change unless an action changes it. So,
your navigation mutation may look at app state to see if there is a need for
data and use that to specify a remote load via the `:remote` section of the
return map.

## HTML5 Routing

Reconciler is in the app, which you should save to an atom. Events come 
in: you decide if routing is currently allowed (unsaved changes?), then
use the reconciler to transact the state to show the proper screen. Imagine
all of your screen switching controlled by idents (like little train track
switches redirecting the UI tree). 
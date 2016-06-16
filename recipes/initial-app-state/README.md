# Initial App State

This recipe shows how to use the InitialAppState protocol to establish an initial application state by
co-locating initial state with the UI components.

## Base case

The basic case is very simple. Just add a `static InitialAppState` protocol onto your defui, and mirror the query
structure with application data. See `app/ui.cljs`. Calling `initial-state` allows you to nest child initial state
into your component, so that local reasoning is preserved.

## Unions

Unions are an interesting case. There are actually two cases to consider: the to-many, and to-one.

## To-one union

In the to-one case, we have some number of union query branches, but only one thing that the app state is currently
pointing to. This case is very common when using union queries to switch UI components (e.g. tabs). In this case
you may want all of the branches to be initialized, even though only one of them is joined into the graph at the start.

In this case (shown in PaneSwitcher in ui.cljs):

1. Compose the union component into the parent as you would for any other app state
2. In the union component itself, define the InitialAppState to return the state of the specific branch component that
is the "default".
3. Make sure that every branch component (child) of the union component has InitialAppState.

Untangled will detect this case during startup, and automatically put the initial state of the other branch components
into top-level tables using the ident function of the union component.

## To-many union

In this case it is more explicit:

1. Compose the union component into the parent as you would for any other app state
2. In the union component itself, define the InitialAppState to return a vector containing the various types
of things that should start out in the component.

There is nothing else to do, since you've explicitly placed the correct initial state into the tree.


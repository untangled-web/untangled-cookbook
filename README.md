# Untangled Cookbook

This cookbook is intended to cover use-cases of how to do common tasks for single-page webapps using the 
Untangled Web Framework.

The recipes themselves are in the `recipes` subdirectory, and each has a recipe `README.md`. This document
acts as an index of them.

Each of them is set up for use in IntelliJ, emacs, or vim. They can also be started from a plain command line.

[PLEASE ADD/VOTE FOR THE TOPICS BELOW VIA THIS LINK](https://github.com/untangled-web/untangled-cookbook/wiki)

# Running Recipes

Each recipe has a project file. You can run them in the environment of your choice. The basic idea is to use a 
plain clojure main REPL for figwheel, and any kind of REPL for the server. The servers in the full-stack recipes
are pre-configured to appear at port 8080. No need to create a config file separately.

## Running Figwheel:

```
lein run -m clojure.main script/figwheel.clj
```

If the recipe has only a UI, then browse to: http://localhost:3449. If
the recipe is full-stack, continue to the next section.

## Running the server:

```
lein run -m clojure.main
user> (go)
```

You should now be able to see the app at: http://localhost:8080.

# Recipes

## UI

- [Managing lists of data](recipes/lists)
- [Using InitialAppState protocol to build starting application state](recipes/initial-app-state)
- [Create a tabbed interface. Includes how to lazy-load initial tab content](recipes/tabbed-interface)
- [Using component local state](recipes/component-local-state)
- [Use om-css to create co-located CSS for reusable styled components](recipes/css)

## Security 

- Server-side security for UI-generated queries
- Integrate OAuth/OpenID for authentication

## Full-stack Interactions

- [Error Handling](recipes/error-handling)
- [Background parallel queries](recipes/background-loads)
- [Run a load from within a mutation (see Paginate a very large list)](recipes/paginate-large-lists)
- [Showing visible indicators while lazy loads are in progress](recipes/lazy-loading-visual-indicators)
- [Some sample uses of the general load function](recipes/load-samples)
- Return a value from a mutation - You cannot. Untangled/Om do not work this way. The method to get this effect
is to include a remote read in your mutation. You can do this via `load-data-action` in your mutation function
(see [Run a load from within a mutation (see Paginate a very large list)](recipes/paginate-large-lists)), or by
including a `(load-data)` abstract call in your top-level `transact!` (see the [Getting Started video on server
interactions at approximately 00:24:56](https://youtu.be/t49JYB27fv8?t=24m56s)).
- Modify/examine headers/cookies on a network request
- Generate a new entity (UI/Server)
- Cache invalidation
- Update data on the server
- Delete an item (UI/server)
- Add autocompletion to a form field
- Reject a transaction on the server and handle that on the client
- [Paginate a very large list (with lazy loading)](recipes/paginate-large-lists)

## Server push

- [Using websockets for all network traffic](recipes/websockets)

## Server-side

- [Server-side security for UI-generated queries](recipes/server-query-security)
- Create and integrate a custom server component
- Add a Server Route that generates an image.
- Interface with SQL on the server
- Add a Ring handler in front of API processing
- Add a Ring handler after the processing chain

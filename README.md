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

- Build a multi-use dialog
- [Create a tabbed interface. Includes how to lazy-load initial tab content](recipes/tabbed-interface)
- Validate forms against the server in real-time
- Hook up HTML5 routing
- Update the UI on a time schedule
- Display multiple views of the same data in two places
- Managing lists of data
- 

## Security 

- Server-side security for UI-generated queries
- Integrate OAuth/OpenID for authentication

## Full-stack Interactions

- Modify/examine headers/cookies on a network request
- Generate a new entity (UI/Server)
- Cache invalidation
- Update data on the server
- Delete an item (UI/server)
- Add autocompletion to a form field
- Reject a transaction on the server and handle that on the client
- Paginate through a very large list of items (while also lazily loading them from a server)

## Server push

- Replace the Untangled transit-based networking layer with Sente
- Push live data updates from the server with Sente

## Server-side

- [Server-side security for UI-generated queries](recipes/server-query-security)
- Create and integrate a custom server component
- Add a Server Route that generates an image.
- Interface with SQL on the server
- Add a Ring handler in front of API processing
- Add a Ring handler after the processing chain


# Untangled Cookbook

This cookbook is intended to cover use-cases of how to do common tasks for single-page webapps using the 
Untangled Web Framework.

The recipes themselves are in the `recipes` subdirectory, and each has a recipe `README.md`. This document
acts as an index of them.

Each of them is set up for use in IntelliJ, emacs, or vim. They can also be started from a plain command line.

# Recipes

## UI

- Build a multi-use dialog
- [Create a tabbed interface](recipes/tabbed-interface)
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

- Create and integrate a custom server component
- Add a Server Route that generates an image.
- Interface with SQL on the server
- Add a Ring handler in front of API processing
- Add a Ring handler after the processing chain


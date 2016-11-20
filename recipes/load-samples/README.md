# Load Samples

This is a simple app that shows a couple of ways to use the `load` API to
load lists of things from the server, and also refresh them based on idents.
This API is available in untangled client 0.6.0+.

See `src/client/app/core.cljs` for examples of using load at startup to
pull in specific targeted items.

See `src/client/app/ui.cljs` for an example (around line 21) of loading
a specific entity as a refresh.

See `src/server/app/api.clj` for examples of implementing the server 
queries and responses.

## Running 

This is a full-stack example. Run both the server and client as explained in the top-level cookbook README.


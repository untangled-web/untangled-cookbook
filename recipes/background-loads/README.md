# Parallel Loading

This is a very basic app designed to demonstrate parallel loading 
(available in 0.4.9-SNAPSHOT+).

The basic gist is that you can add `:parallel true` to a `load-field` or `load-data` and have that 
skip the sequential network queue so that multiple of them can run in parallel. See the ui.cljs for
the example code.

## Running 

This is a full-stack example. Run both the server and client as explained in the top-level cookbook README.

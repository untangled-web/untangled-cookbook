# Error Handling

This is a very basic app designed to show the fewest steps needed for a
complete full-stack implementation of error handling in Untangled.

Each section below has a brief explanation, and there are comments in
the source code that explain more in context.

## Global Error Marker

Untangled Client stores the most recently received server error at the
top level of the app state, under the key `:untangled/server-error`.
The error will be stored at this key until either you explicitly remove
it from the app state (via some mutation), or until the client receives
a new error from the server. A new server error will overwrite the
previous one.

## Global Error Callback

When calling `new-untangled-client`, you can add an error callback that
will be triggered every time an error is received from the server. So,
immediately after the global error marker is set, the global error
callback is triggered. Implement your callback as a function of three
arguments, the first is the global app state atom, the second is the
status code and the third one is the error exactly as the server returns
it.

```
(untangled.client.core/new-untangled-client
 ;; this function is called on *every* network error, regardless of cause
 :network-error-callback
 (fn [state status-code error]
    (log/warn "Global callback:" error " with status code: " status-code)))
```

So, based on the error, you can make modifications to your app-state
as desired.

Though this cookbook does not have an example of making a custom
untangled network, you can also add a global error callback to custom
networks.

## Data Fetch Fallback

When calling `load-field`, `load-data`, `load-field-action`, or
`load-data-action`, you can add a fallback mutation symbol. If the
server fails when loading the requested data, then the mutation
specified by the fallback symbol will be called. The app state atom will
be placed in the mutation environment, and the server error will be
placed in the mutation params under the `:error` key.

So, you have access to the same data that you have access to in the
global error callback. However, this fallback mutation will only be
called when the server fails on a given call to the load function where
the fallback symbol was appended. All other load functions that fail,
and all mutations that fail, will not trigger a data fetch fallback.

## Mutation Fallback

When calling `om/transact!` to trigger a mutation, you can tack an
 additional fallback mutation on to the end of the query:
 ```
 (om/transact! this [(my/mutation) (tx/fallback {:action 'my/fallback})])
 ```
 Your mutation will be sent to the server (assuming you have set
 `:remote true` in the mutation), and the fallback will be stripped out
 before your mutation is sent over the wire. If your mutation is
 successful on the server, the client will not call the fallback. If
 your mutation fails on the server, the client is notified of the
 failure and it proceeds to find the mutation specified by the symbol
 at the `:action` key in the fallback params, and executes that mutation.

 The function signature for the `tx/fallback` triggered mutations is the
  same as the function signature for the data fetch fallback mutations.

## Server Errors

On the server, any error that you throw within the read or mutate
functions is propagated through the ring handler into the response
that is sent back to the client. All thrown errors are interpreted as
being an internal server error, status code 500, UNLESS you throw an error
matching the Untangled error handling protocol.

Untangled allows customized errors if they are created as follows:

1. Must be a clojure ExceptionInfo.
2. The keys for the data map passed into `(ex-info)` must be a subset of the keys
`:status :headers :body`.
3. The map at `:headers` cannot have a key of `"Content-Type"`. Untangled uses
transit-json to communicate between client and server, and this cannot be changed.

Since om handles multiple mutation errors by sending back one map,
we return a status code of 400 if any one mutation fails within a
set of mutations. The body of that response is the one map of all failed
mutations. So, you are responsible for checking the returned map
on the client for the status codes, headers, and body specific to each mutation.
See the javascript console in the running cookbook recipe to see the structure
of mutation errors as they are returned from the server.

# Takeaways

Untangled Client allows you to handle server errors in 3 ways:

1. A global error callback, triggered for every server failure.
2. Adding a `tx/fallback` mutation after your desired mutation to
recover the app state should the client-side mutation succeed but the
server-side mutation fails.
3. Adding a `:fallback` mutation to data fetch loads to take a desired
action if the server doesn't respond with the requested data.

Untangled Server allows you to customize errors by:

1. Throwing a clojure ExceptionInfo object with a map containing the
error status code (>= 400), any custom headers excluding content type,
and a response body.
2. Throwing any java Exception, which will return with status code 500
and the type of the Exception.

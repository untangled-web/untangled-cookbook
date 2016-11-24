# Return a Value From a Server Mutation

The recommended approach for this is to issue a remote follow-on read. In most cases this is better because
what you really want to do is pull updated data from the server (which will need to be merged with your app
state, and might even require some kind of post mutation). You can do this via `load-data-action` in your mutation
function (see [Run a load from within a mutation (see Paginate a very large list)](recipes/paginate-large-lists)), or by
including a `(load-data)` abstract call in your top-level `transact!` (see the [Getting Started video on server
interactions at approximately 00:24:56](https://youtu.be/t49JYB27fv8?t=24m56s)).

As of 0.6.0 Untangled does let you hook into the merge stage of Om Next, which is where return values from mutations
are available (stock Om just throws away these values, and you have to override merge). Thus, Untangled is giving
you a defined way of getting to the return values without having to specifically mess with the complications
in the merge stage.

You do this during client construction by providing a `:mutation-merge` option to `new-untangled-client`. The value of
this argument should be a `(fn [state-map mutation-symbol return-value] new-state-map)`.

## The Sample Code

The `app.core` namespace and `app.mutations` namespace on the client side contain the relevant code for this example. 
The server `app/api` contains the relevant code.

## Notes

The main disadvantage of dealing with return values in Om is that there are a few things missing at merge. Most specifically,
there is no query to use for normalizing the data into the app database. Thus, return values 
are best used for really simple scalar values like status info. Another problem is
that Om schedules renders based on the component from which the mutation was run. So,
if the return value is used to manipulate arbitrary state you will have to
use follow-on (local) reads to get your UI to properly refresh. This is a
relatively normal thing to need to do, but it is something to keep track of.

The return value of the server-side method must return a map or nil. Normally the only thing
you'd return in the map is `:tempids`. You may include any additional key-value pairs for
your client-side merge handler. In the example we're just using `:value`.

## Running the Recipe. 

See the top-level README. This is a full-stack example.

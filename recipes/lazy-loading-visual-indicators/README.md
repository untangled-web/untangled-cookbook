# Loading Visual Indicators

Untangled has two different ways to detect if network traffic is active:

- A top-level key named `:ui/loading-data`
- Markers placed in app-state for targeted loads

The first can be queried from any component using a link:

```
(query [this] [:a :b [:ui/loading-data '_]])
```

and can be treated as a boolean. The included source uses this to
toggle the display style of a right-floated global indicator.

The second form is made available when you use `load-field` (which in
turn knows the Ident of the component and therefore the app state target
location of the field data). Thus:

```
(load-field this :comments)
```

will place a network loading status marker in place of the `:comments`
result. This marker includes a field called `:ui/fetch-state`. The
helper function `lazily-loaded` can be used to show alternative things
for such an item. It takes a renderer for rendering the real data,
the data that the current ui query is returning, and an optional named
list of renderers for the various states (`:not-preset-render`,
`:loading-render`, etc.).

**The only caveat is that IF the field is a join into a child**, then that
child MUST query for `:ui/fetch-state` or the call to `lazily-loaded`
will not get the current state, and will render incorrectly.

See ui.cljs for most of the details. 

# Running this recipe

This is a full-stack example, so use the top-level README instructions on
running the client and server. 

Once running, you should see a "Load Child" button. Pressing that will
run the query on the server (which has a built-in 1 second delay so you
can see things happen). The child will appear with a "Load items" button.
Pressing that should do the same kind of load for the items.

Note the location of `:ui/fetch-state` in the queries. Note again that
the target **child** must query for the fetch state, since it's state
is what is being temporarily replaced by a load marker (e.g. you're 
querying the load marker during load, not the real database result).

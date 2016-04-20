# Query Security

IN PROGRESS...NO CODE YET, BUT DESCRIPTION IS GOOD

This recipe walks you through a suggestion on handling query security. It
uses Spectre to walk the query data structure.

## Running the code

You want to run both a figwheel build and a server. See the top-level
README for instructions. Be sure you access the app through the server
port or the remote load will not work.

## UI Query Security

If you examine any UI query it will have a tree form. That is the nature of Datomic pull syntax
and UI's. For any such query, you can imagine it as a graph walk:

Take this query:

```
[:a {:join1 [:b {:join2 [:c :d]}]}]
```

```
   QUERY PART                            IMPLIED DATABASE graph
   [:a {:join1                           { :a 6 :join1 [:tableX id1] }
                                                          \
                                                           \
                                                           \|
           [:b {:join2                       :tableX { id1 { :id id1 :join2 [:tableY id2]
                                                                            /
                                                                           /
                                                                          |/
                  [:c :d]}]}]                :tableY { id2 { :id id2 :c 4 :d 5 }}
```

One idea that works pretty well for us is based on this realization: There is a starting point of this walk...and
it *must* be specified (or implied at least) by the incoming query. A tradition logic check needs to be run on 
this object to see if it is OK for the user to *start* reading the database there. 

Two things remain from there:

1. Verify the user is allowed to read the specific *data* at the node of the graph (e.g. :a, :c, and :d)
2. Verify the user is allowed to *walk* across a given *reference* at that node of the graph.

However, since both of those cases are essentially the same in practice (can the user read the given property), the
algorithm simplifies to:

- Verify the user is allowed to read the "top" object. If not, disallow the query.
- Create a whitelist of keywords that are allowed to be read by the query in question. This can be a one-time
  declarative configuration, or something dynamic based on user rights.
- Walk the query, and gather up all keywords
- Find the set difference of the whitelist and the query keywords.
- If the difference if not empty, refuse to run the query

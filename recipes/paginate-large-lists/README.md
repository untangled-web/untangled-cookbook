# Paginating Large Lists

This recipe explores a method of paginating a very large result set, and
the user is interested in viewing them by pages. It is likely that the user
will only look at the first few pagess, so you'd prefer to lazy-load the
results as they are needed.

This is mostly a data structure problem, but it is useful to see how
you would approach managing the data structures with Untangled.

This recipe also uses data loading from within a mutation, so it is 
educational for that as well.

## The Approach

We place two different data structures into the application state on the
client: A cache of the items loaded so far, and a view of the
current page (which is a sub-set (page) of the cached items).

The `:current-page` data structure tracks details about where the view
is currently, and the cache maintains the loaded items.

```
{:list/cache   []
 :current-page {:page-size 10          ; how many items to show on a page
                :total-results 20000   ; how many items there are
                :start     0           ; The current page offset
                ; the current visible items
                :items     [... 10 items ...]}})
```

Of course, if you wanted to do something more fancy (like being able to jump to a 
specific page) you'd either want to only cache a single page at 
a time on the client, or create a much more complicated data structure
to hold the cached items.

The overall mechanism is as follows:

```
Initial Load -> Results arrive -> Post mutation integrates them in cache and updates view (:current-page)
```

```
User presses Next -> Mutation runs -- cache hit --> Update current page
                                   \
                                    \- cache miss -> Trigger load -> results -> post-mutation (like initial load)
```

More advanced interactions (jump to page, clearing cached items that are far away from
the current position, etc.) could be done in exactly the same manner, 
assuming sufficient data structures for tracking what is going on. You could
even combine this with background loading to get multiple pages loading in 
parallel (again, assuming you made the cache itself a bit more capable).

# Form Wizard

This recipe uses the forms support from Untangled UI to show a full-stack
wizard that persist's the user's responses into a data store (in this case
a server-side in-memory atom).

The user is allowed to move in either direction in the wizard, and the 
wizard includes a decision graph that can change based on the responses:

```
                                         +- m --> Do you mind shaving?
                                         |
   Start (overview slide) ---> Gender? --|
                                         |
                                         +- f --> Do you like men with beards?
```

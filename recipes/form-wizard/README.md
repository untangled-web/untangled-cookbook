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

Once the user completes the survey and the results have submitted to the server, the app queries
the server for the survey results and shows them. The UI does not let them go back after submission.
Reload the page to take the survey again.

This recipe uses the Untangled UI library for the following features:

- Rendering helpers for a prettier look and feel
- Forms support for tracking the responses and sending them to the server

The source code includes a number of comments to help you understand the details.

# Running It

This is a full-stack example. See the top-level Cookbook README.md and follow those
common instructions.

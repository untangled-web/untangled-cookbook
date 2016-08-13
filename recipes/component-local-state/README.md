# Component Local State

This recipe demonstrates some common use-cases for component local state. In general you want to avoid component
local state because it violates a basic useful property of Untangled components: That they are pure functions. If
you locate all of your state in the central application database, then you get a variety of advantages, such as
easier reasoning about the rendering, and support from the support viewer being able to show the component
correctly for the state of the application.

However, in some cases it is reasonable to relax and use local state. The two most common cases are:

- Text input fields. You'd rather let the user interact for some period of time and checkpoint the value into app state.
  This prevents uninteresting things from appearing in the app state history (e.g. every keystroke of input).
- The cost of updating the central atom, running a database query, and re-rendering are too much overhead. An example
of this is trying to render some kind of animation or interactive graphical interaction within a component.

## Running 

This is a client-only example. Run the client as explained in the top-level cookbook README.

## Form Field Details

## Canvas Details

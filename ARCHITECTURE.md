# Architecture

The goal of the [Whiley
Compiler](https://github.com/Whiley/WhileyCompiler/) is to take one or
more `whiley` files along with zero or more `wyil` files (i.e. package
dependencies) and produce a single `wyil` file.  The `wyil` format is
the Whiley Intermediate Language which, roughly speaking, provides an
[Abstract Syntax
Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree)
representation of the `whiley` files.  A key point here is that the
compiler only produces `wyil` files and does not, for example,
generate JavaScript files or other executable formats (these are
handled by other components of the build tool).

The Whiley Compiler is responsible for (amongst other things) parsing
and type checking Whiley source files and reporting any errors it
finds.  The compiler employs a number of _pipeline stages_ for doing
various aspects of its work.  For example, _name resolution_ and _flow
type checking_ are two important stages in the compiler.  Note,
however, that the compiler does not perform any [compiler
optimisations](https://en.wikipedia.org/wiki/Optimizing_compiler) as
such things are left to the various backends controlled by the build
tool.

## Pipeline Stages

A summary of the main stages employed during compilation is as
follows:

   * **Name Resolution**.  Responsible for determining the _full
       qualification_ of any name used within a source file.  Such
       names can represent both _local_ and _external_ entities.  To
       determine which entity a given name refers to, this pass
       traverses any `import` statements given in corresponding
       `whiley` file and searches their contents.  As part of this
       process, symbols from external files are _linked_ into the
       `wyil` file being generated either in full or as _stubs_.
   
   * **Flow Typing**.  Perhaps the largest and most complex part of
       the compiler.  This is responsible for determining a single
       type for every expression used within a Whiley source file and,
       furthermore, checking that such types are used appropriately.
       The Whiley language complicates this process as it supports
       _flow typing_ which allows variables to have different types at
       different program points.  Another challenge is that Whiley
       allows generic types to be ommitted within expressions and
       employs [type
       inference](https://en.wikipedia.org/wiki/Type_inference) to
       determine what was intended.
   
   * **Definite Assignment / Unassignment**.  Responsible for ensuring
       that every variable is _definitely assigned_ before it is used
       within an expression.  Furthermore, that variables marked
       `final` are assigned at most once.  two simple [dataflow
       analyses](https://en.wikipedia.org/wiki/Definite_assignment_analysis)
       are used to do this.
   
   * **Functional Purity**.  Responsible for ensuring that impure code
       is not used in a pure setting (such as in a [pure
       function](https://en.wikipedia.org/wiki/Pure_function)).  For
       example, a `function` in Whiley cannot call a `method` (since
       these are impure and may have side effects).

   * **Move Analysis**.  Responsible for determining when a value of a
       dynamically sized data type can be _moved) or must be
       _copied_. Moving is preferable (when permitted) because then
       the original reference can be used without cloning the
       underlying data (hence, is more efficient).

   * **Miscellaneous**.  There are a number of other miscellaneous
       things performed.  For example, ensuring static variable
       initialisers are not cyclic.  Likewise, determining the
       _positional variance_ of template parameters in declared types.

Each of these stages can produce errors, and the compiler endeavours
to continue as far as possible in the presence of errors.

## Intermediate Language

### Interpreter

### Syntactic Heaps

### Error Markers

### Spans
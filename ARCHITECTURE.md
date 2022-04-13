# Architecture

The goal of the [Whiley Compiler](https://github.com/Whiley/WhileyCompiler/) is to take one or more `whiley` files along with zero or more `wyil` files (i.e. package dependencies) and produce a single `wyil` file.  The `wyil` format is the Whiley Intermediate Language which, roughly speaking, provides an [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) representation of the `whiley` files.  A key point here is that the compiler only produces `wyil` files and does not, for example, generate JavaScript files or other executable formats (these are handled by other components of the build tool).

The Whiley Compiler is responsible for (amongst other things) parsing and type checking Whiley source files and reporting any errors it finds.  The compiler employs a number of _pipeline stages_ for doing various aspects of its work.  For example, _name resolution_ and _flow type checking_ are two important stages in the compiler.

## Pipeline Stages

The current set of stages employed in the compiler is (in order) as
follows:

   * **Name Resolution**.  This is responsible for determining the
       _full qualification_ of any name used within a source file.
       Such names can represent both _local_ and _external_ entities.
       To determine which entity a given name refers to, this pass
       traverses any `import` statements given in corresponding
       `whiley` file and searches their contents.   
   * **Recursive Type Check**.
   * **Flow Typing**.
   * **Definite Assignment**.
   * **Definite Unassignment**.
   * **Functional Purity**.
   * **Static Variables**.
   * **Unsafe Code**.
   * **Miscellaneous**. 

## Intermediate Language

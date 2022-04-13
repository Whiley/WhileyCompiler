# Installation

The easiest way to get started with Whiley is by running it in your web-browser on [whileylabs.com](http://whileylabs.com).  To run Whiley from the command-line (e.g. `wy build`), see instructions for installing the [Whiley Build Tool](https://github.com/Whiley/WhileyBuildTool).  You can find more information about Whiley at [whiley.org](https://whiley.org).

# Contributing

When making a contribution to the Whiley project, you will need to
first sign-off the developers certificate of origin.  Please see the
CONTRIBUTORS file for more details.

# Architecture

The Whiley Compiler (WyC) is one component of the larger [Whiley Build Tool](https://github.com/Whiley/WhileyBuildTool).  When you run `wy build` on the command line, you are running the build tool which, in turn, runs the Whiley Compiler (amongst other things).  The goal of the Whiley Compiler is to take one or more `whiley` files along with zero or more `wyil` files (i.e. package dependencies) and produce a single `wyil` file.  The `wyil` format is the Whiley Intermediate Language which, roughly speaking, provides an [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) representation of the `whiley` files.  A key point here is that the compiler only produces `wyil` files and does not, for example, generate JavaScript files or other executable formats (these are handled by other components of the build tool).

The Whiley Compiler is responsible for (amongst other things) parsing and type checking the Whiley files and will report errors if it finds them.  The compiler employs a number of _pipeline stages_ for doing various aspects of its work.  For example, _name resolution_ and _flow type checking_ are two important stages in the compiler.

## Pipeline Stages

## Intermediate Language

import whiley.lang.*

method main(System.Console console):
    &{int x} c = new {x: 5}
    *c = {x: 4}
    console.out.println( *c )


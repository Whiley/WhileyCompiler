import whiley.lang.*

method main(System.Console console):
    &int c = new 5
    *c = 4
    console.out.println( *c )


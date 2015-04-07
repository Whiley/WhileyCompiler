import whiley.lang.*

method main(System.Console console):
    &[real] b = new [1.0, 2.0, 3.0]
    real x = (*b)[1]
    console.out.println(x)

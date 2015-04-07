import whiley.lang.*

type Rec is &{ [real] items }

method main(System.Console console):
    Rec r = new { items: [1.0, 2.0, 3.0] }
    real x = (r->items)[1]
    console.out.println(x)



type Rec is &{ [real] items }

public export method test():
    Rec r = new { items: [1.0, 2.0, 3.0] }
    real x = (r->items)[1]
    assume x == 2.0

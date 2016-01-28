

type Rec is &{ bool[] items }

public export method test():
    Rec r = new { items: [false, true, false] }
    bool x = (r->items)[1]
    assume x == true

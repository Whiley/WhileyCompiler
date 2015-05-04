

type Rec is { [real] items }

public export method test():
    Rec r = { items: [1.0, 2.0, 3.0] }
    real x = (r.items)[1]
    assert x == 2.0

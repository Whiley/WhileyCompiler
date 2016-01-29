

type Rec is { bool[] items }

public export method test():
    Rec r = { items: [true, true, false] }
    bool x = (r.items)[1]
    assert x == true

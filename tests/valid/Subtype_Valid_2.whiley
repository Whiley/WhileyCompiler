

type sr7nat is int

public export method test() :
    {int f} x = {f: 1}
    x.f = x.f + 1
    assume x == {f:2}



type sr5nat is int

public export method test() :
    {sr5nat f} x = {f: 1}
    x.f = 2
    assert x == {f: 2}

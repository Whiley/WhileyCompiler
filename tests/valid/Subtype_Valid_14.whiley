

type sr7nat is (int n) where n > 0

public export method test() -> void:
    {sr7nat f} x = {f: 1}
    x.f = x.f + 1
    assert x == {f: 2}

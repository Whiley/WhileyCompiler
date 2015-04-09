import whiley.lang.*

type sr5nat is int

method main(System.Console sys) -> void:
    {sr5nat f} x = {f: 1}
    x.f = 2
    assert x == {f: 2}

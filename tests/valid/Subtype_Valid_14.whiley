import whiley.lang.*

type sr7nat is (int n) where n > 0

method main(System.Console sys) -> void:
    {sr7nat f} x = {f: 1}
    x.f = x.f + 1
    sys.out.println(x)

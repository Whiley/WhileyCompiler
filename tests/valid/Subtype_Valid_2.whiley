import whiley.lang.System

type sr7nat is int

method main(System.Console sys) => void:
    {int f} x = {f: 1}
    x.f = x.f + 1
    sys.out.println(Any.toString(x))

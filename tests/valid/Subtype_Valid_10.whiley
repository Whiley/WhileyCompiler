import whiley.lang.System

type sr5nat is (int n) where n > 0

method main(System.Console sys) => void:
    x = {f: 1}
    x.f = 2
    sys.out.println(Any.toString(x))

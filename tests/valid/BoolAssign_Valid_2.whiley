import whiley.lang.*

method main(System.Console sys) -> void:
    bool x = true
    bool y = false
    sys.out.println(x)
    sys.out.println(y)
    sys.out.println_s("AND")
    x = x && y
    sys.out.println(x)
    sys.out.println_s("NOT")
    sys.out.println(!x)

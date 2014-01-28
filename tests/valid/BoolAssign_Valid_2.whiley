import whiley.lang.System

method main(System.Console sys) => void:
    bool x = true
    bool y = false
    sys.out.println(x)
    sys.out.println(y)
    sys.out.println("AND")
    x = x && y
    sys.out.println(x)
    sys.out.println("NOT")
    sys.out.println(!x)

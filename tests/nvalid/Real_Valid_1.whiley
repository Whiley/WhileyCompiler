import println from whiley.lang.System

method main(System.Console sys) => void:
    x = 1.20
    y = 2.40
    sys.out.println(x + y)
    sys.out.println(y - x)
    sys.out.println(x / y)
    sys.out.println(x * y)

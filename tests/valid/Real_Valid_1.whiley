import whiley.lang.*

method main(System.Console sys) -> void:
    real x = 1.20
    real y = 2.40
    sys.out.println(x + y)
    sys.out.println(y - x)
    sys.out.println(x / y)
    sys.out.println(x * y)

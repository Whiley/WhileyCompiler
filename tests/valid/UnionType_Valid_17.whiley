import whiley.lang.System

type IntList is int | [int]

method f(System.Console sys, int y) => void:
    sys.out.println(Any.toString(y))

method g(System.Console sys, [int] z) => void:
    sys.out.println(Any.toString(z))

method main(System.Console sys) => void:
    IntList x = 123
    f(sys, x)
    x = [1, 2, 3]
    g(sys, x)

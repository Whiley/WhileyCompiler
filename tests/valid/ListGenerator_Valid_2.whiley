import whiley.lang.System

method f(System.Console sys, [int] x) => void:
    z = |x|
    sys.out.println(Any.toString(z))
    sys.out.println(Any.toString(x[z - 1]))

method main(System.Console sys) => void:
    arr = [1, 2, 3]
    f(sys, arr)

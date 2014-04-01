import whiley.lang.System

type pos is int

type neg is int

type intlist is pos | neg | [int]

function f(intlist x) => int:
    if x is int:
        return x
    return 1

method main(System.Console sys) => void:
    int x = f([1, 2, 3])
    sys.out.println(Any.toString(x))
    x = f(123)
    sys.out.println(Any.toString(x))

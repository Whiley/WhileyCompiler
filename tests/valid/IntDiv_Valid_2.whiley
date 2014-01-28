import whiley.lang.System

function f(int x, int y) => int:
    return x / y

method main(System.Console sys) => void:
    x = f(10, 2)
    sys.out.println(Any.toString(x))

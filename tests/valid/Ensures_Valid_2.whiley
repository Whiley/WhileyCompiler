import whiley.lang.System

function f(int x) => (int y)
ensures y > x:
    x = x + 1
    return x

method main(System.Console sys) => void:
    int y = f(1)
    sys.out.println(Any.toString(y))

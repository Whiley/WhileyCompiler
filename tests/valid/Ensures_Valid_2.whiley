import whiley.lang.*

function f(int x) -> (int y)
ensures y > x:
    x = x + 1
    return x

method main(System.Console sys) -> void:
    int y = f(1)
    sys.out.println(y)

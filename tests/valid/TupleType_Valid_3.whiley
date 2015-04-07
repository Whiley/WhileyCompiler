import whiley.lang.*

function f(int x) -> (int, int):
    return (x, x + 2)

method main(System.Console sys) -> void:
    int x, int y = f(1)
    sys.out.println(x)
    sys.out.println(y)

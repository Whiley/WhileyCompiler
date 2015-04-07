import whiley.lang.*

function f(int x) -> (int r)
ensures r > x:
    //
    x = x + 1
    return x

method main(System.Console sys) -> void:
    int y = f(1)
    sys.out.println(y)

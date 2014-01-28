import whiley.lang.System

function f(int x, int y) => (int r)
requires (x >= 0) && (y >= 0)
ensures r > 0:
    //
    bool a = x == y
    if a:
        return 1
    else:
        return x + y

function g(int x, int y) => (int r)
requires (x >= 0) && (y >= 0)
ensures r > 0:
    //
    bool a = x >= y
    if !a:
        return x + y
    else:
        return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1, 1)))
    sys.out.println(Any.toString(f(0, 0)))
    sys.out.println(Any.toString(f(4, 345)))
    sys.out.println(Any.toString(g(1, 1)))
    sys.out.println(Any.toString(g(0, 0)))
    sys.out.println(Any.toString(g(4, 345)))

import * from whiley.lang.*

function f(int x) -> int throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

function g(int x) -> int:
    return x

method main(System.Console sys) -> void:
    int|real x = 1
    //
    try:
        sys.out.println(Any.toString(f(1)))
        x = 1.02
        sys.out.println(Any.toString(f(0)))
        sys.out.println(Any.toString(f(-1)))
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION: " ++ g(x))
    sys.out.println("DONE")

import * from whiley.lang.*

function f(int x) -> int:
    return x

method main(System.Console sys) -> void:
    int x = 1
    try:
        sys.out.println(Any.toString(f(1)))
        sys.out.println(Any.toString(f(0)))
        sys.out.println(Any.toString(f(-1)))
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION: " ++ x)
    sys.out.println("DONE")

import whiley.lang.System

function f(real x) -> int throws ASCII.string:
    if x >= 0.0:
        return 1
    else:
        throw "error"

method main(System.Console sys) -> void:
    try:
        sys.out.println(Any.toString(f(1.0)))
        sys.out.println(Any.toString(f(0.0)))
        sys.out.println(Any.toString(f(-1.0)))
    catch(ASCII.string e):
        sys.out.println("CAUGHT EXCEPTION: " ++ e)
    sys.out.println("DONE")

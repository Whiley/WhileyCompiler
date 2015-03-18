import whiley.lang.System

function f(real x) -> int throws ASCII.string:
    if x >= 0.0:
        return 1
    else:
        throw "error"

method main(System.Console sys) -> void:
    try:
        sys.out.println(f(1.0))
        sys.out.println(f(0.0))
        sys.out.println(f(-1.0))
    catch(ASCII.string e):
        sys.out.println_s("CAUGHT EXCEPTION: " ++ e)
    sys.out.println_s("DONE")

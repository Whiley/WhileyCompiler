import whiley.lang.System

function f(int x) -> int throws ASCII.string | int:
    if x >= 0:
        return 1
    else:
        if x == -1:
            throw "error"
        else:
            throw x

method missed(System.Console sys, int x) -> void throws ASCII.string:
    try:
        f(x)
    catch(int e):
        sys.out.println_s("CAUGHT EXCEPTION (int): " ++ Any.toString(e))

method main(System.Console sys) -> void:
    try:
        missed(sys, 1)
        missed(sys, -2)
        missed(sys, -1)
    catch(ASCII.string e):
        sys.out.println_s("CAUGHT EXCEPTION (ASCII.string): " ++ e)

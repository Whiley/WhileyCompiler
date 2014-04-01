import whiley.lang.System

function f(int x) => int throws string | int:
    if x >= 0:
        return 1
    else:
        if x == -1:
            throw "error"
        else:
            throw x

method missed(System.Console sys, int x) => void throws string:
    try:
        f(x)
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION (int): " ++ Any.toString(e))

method main(System.Console sys) => void:
    try:
        missed(sys, 1)
        missed(sys, -2)
        missed(sys, -1)
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION (string): " ++ e)

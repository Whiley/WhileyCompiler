import whiley.lang.System

function f(int x) => int throws string | int:
    if x > 0:
        return 1
    else:
        if x == -1:
            throw "error"
        else:
            throw x

method g(System.Console sys, int x) => void:
    try:
        f(x)
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION (string): " ++ e)
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION (int): " ++ Any.toString(e))

method main(System.Console sys) => void:
    g(sys, 1)
    g(sys, 0)
    g(sys, -1)

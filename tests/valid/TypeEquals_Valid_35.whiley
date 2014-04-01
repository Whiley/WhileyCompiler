import whiley.lang.System

type pos is int

type neg is int

type expr is pos | neg | [int]

function f(expr e) => string:
    if (e is pos) && (e > 0):
        e = e + 1
        return "POSITIVE: " ++ Any.toString(e)
    else:
        return "NEGATIVE: " ++ Any.toString(e)

method main(System.Console sys) => void:
    sys.out.println(f(-1))
    sys.out.println(f(1))
    sys.out.println(f(1234))

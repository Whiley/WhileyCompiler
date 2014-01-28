import whiley.lang.System

type pos is real

type neg is int

type expr is pos | neg | [int]

function f(expr e) => string:
    if (e is pos) && (e > 0):
        return "POSITIVE: " ++ Any.toString(e)
    else:
        if e is neg:
            return "NEGATIVE: " ++ Any.toString(e)
        else:
            return "OTHER"

method main(System.Console sys) => void:
    sys.out.println(f(-1))
    sys.out.println(f(1.0))
    sys.out.println(f(1.234))
    sys.out.println(f([1, 2, 3]))

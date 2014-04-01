import whiley.lang.System

type ilist is int | [int]

type rlist is real | [int]

function f(rlist e) => string:
    if e is [int]:
        return "[int]"
    else:
        return "real"

function g(ilist e) => string:
    return f(e)

method main(System.Console sys) => void:
    sys.out.println(f(1))
    sys.out.println(f([1]))
    sys.out.println(f([]))

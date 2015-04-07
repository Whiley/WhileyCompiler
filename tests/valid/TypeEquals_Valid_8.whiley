import whiley.lang.*

type ilist is int | [int]

type rlist is real | [int]

function f(rlist e) -> bool:
    if e is [int]:
        return true
    else:
        return false

function g(ilist e) -> bool:
    return f((rlist) e)

method main(System.Console sys) -> void:
    sys.out.println(f(1.0))
    sys.out.println(f([1]))
    sys.out.println(f([]))

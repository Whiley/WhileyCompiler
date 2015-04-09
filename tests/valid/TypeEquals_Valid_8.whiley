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
    assume f(1.0) == false
    assume f([1]) == true
    assume f([]) == true
    assume g(1) == false
    assume g([1]) == true
    assume g([]) == true

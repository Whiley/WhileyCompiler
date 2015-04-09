import whiley.lang.*

type rlist is real | [int]

function f(rlist l) -> int:
    if l is real:
        return 0
    else:
        return |l|

method main(System.Console sys) -> void:
    assume f(123.0) == 0
    assume f(1.23) == 0
    assume f([1, 2, 3]) == 3

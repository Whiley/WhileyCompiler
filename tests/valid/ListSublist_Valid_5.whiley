import whiley.lang.*

type posintlist is [int]

function sum(posintlist ls) -> int:
    if |ls| == 0:
        return 0
    else:
        [int] rest = ls[1..|ls|]
        return ls[0] + sum(rest)

method main(System.Console sys) -> void:
    int c = sum([-12987987234, -1, 2, 409234, 2398729879])
    sys.out.println(c)

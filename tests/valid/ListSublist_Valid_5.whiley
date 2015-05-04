

type posintlist is [int]

function sum(posintlist ls) -> int:
    if |ls| == 0:
        return 0
    else:
        [int] rest = ls[1..|ls|]
        return ls[0] + sum(rest)

public export method test() -> void:
    int c = sum([-12987987234, -1, 2, 409234, 2398729879])
    assume c == -10588848120

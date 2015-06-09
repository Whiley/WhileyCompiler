

type expr is [int] | bool

function g([int] input) -> [int]:
    return input ++ [-1]

function f(expr e) -> [int]:
    if e is [int]:
        [int] t = g(e)
        return t
    else:
        return []

public export method test() -> void:
    [int] e = [1, 2, 3, 4]
    assume f(e) == [1, 2, 3, 4, -1]
    assume f(false) == []

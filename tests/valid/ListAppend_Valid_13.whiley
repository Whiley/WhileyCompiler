

type plistv6 is [int]

function f(plistv6 xs) -> int:
    return |xs|

function g(plistv6 left, plistv6 right) -> int:
    return f(left ++ right)

public export method test() -> void:
    int r = g([1, 2, 3], [6, 7, 8])
    assume r == 6


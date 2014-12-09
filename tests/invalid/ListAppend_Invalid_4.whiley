
type plistv6 is ([int] list) where no { x in list | x < 0 }

function f(plistv6 xs) -> int:
    return |xs|

function g(plistv6 left, [int] right) -> int:
    return f(left ++ right)

method main(System.Console sys) -> void:
    int r = g([1, 2, 3], [-1, 7, 8])
    debug Any.toString(r)

import println from whiley.lang.System

type plistv6 is [int] where no { x in $ | x < 0 }

function f(plistv6 xs) => int:
    return |xs|

function g(plistv6 left, plistv6 right) => int:
    return f(left + right)

method main(System.Console sys) => void:
    r = g([1, 2, 3], [6, 7, 8])
    sys.out.println(Any.toString(r))


function f(int x) -> int
requires x >= 0:
    return x

function f(int x) -> int
requires x >= 0:
    return x

method main(System.Console sys) -> void:
    debug Any.toString(f(1))

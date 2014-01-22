
function f() => int
ensures (2 * $) == 1:
    return 1

method main(System.Console sys) => void:
    debug Any.toString(f())

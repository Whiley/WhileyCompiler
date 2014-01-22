
function f() => (int r)
ensures (2 * r) == 1:
    //
    return 1

method main(System.Console sys) => void:
    debug Any.toString(f())

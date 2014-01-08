
function f([int] ls) => void
requires no { i in {0, 1, 2, 3, 4} | (i >= 0) && ((i < |ls|) && (ls[i] < 0)) }:
    debug Any.toString(ls)

method main(System.Console sys) => void:
    f([-1, 0, 1, 2, 3])

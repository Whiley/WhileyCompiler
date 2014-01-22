
type anat is int where $ >= 0

type bnat is int where (2 * $) >= $

function f(anat x) => int:
    return x

function f(bnat x) => int:
    return x

method main(System.Console sys) => void:
    debug Any.toString(f(1))

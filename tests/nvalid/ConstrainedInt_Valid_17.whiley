import println from whiley.lang.System

type anat is int where $ >= 0

type bnat is int where (2 * $) >= $

function atob(anat x) => bnat:
    return x

function btoa(bnat x) => anat:
    return x

method main(System.Console sys) => void:
    x = 1
    sys.out.println(Any.toString(atob(x)))
    sys.out.println(Any.toString(btoa(x)))

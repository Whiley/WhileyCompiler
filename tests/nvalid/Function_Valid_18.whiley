import println from whiley.lang.*

type nat is int where $ >= 0

function abs(int item) => nat:
    return Math.abs(item)

function nop(nat item) => nat
ensures item == $:
    return Math.abs(item)

method main(System.Console sys) => void:
    xs = abs(-123)
    sys.out.println(Any.toString(xs))
    xs = nop(1)
    sys.out.println(Any.toString(xs))


type c1nat is int where $ > 0

type c1pnat is c1nat where $ > 1

function f(int x) => c1pnat:
    return x

method main(System.Console sys) => void:
    debug Any.toString(f(-1))

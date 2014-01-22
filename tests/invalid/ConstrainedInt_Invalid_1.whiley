
type c1nat is int x where x > 0

type c1pnat is c1nat x where x > 1

function f(int x) => c1pnat:
    return x

method main(System.Console sys) => void:
    debug Any.toString(f(-1))

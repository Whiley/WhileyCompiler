
type scf2nat is (int x) where x >= 0

function f(scf2nat x) => void:
    debug Any.toString(x)
    x = -1
    debug Any.toString(x)
    f(x)

method main(System.Console sys) => void:
    f(1)


type scf1nat is int where $ >= 0

function f(scf1nat x) => int:
    return x

method main(System.Console sys) => void:
    x = -1
    f(x)

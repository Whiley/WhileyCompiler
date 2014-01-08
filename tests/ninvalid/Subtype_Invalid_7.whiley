
type scf7nat is int where $ < 0

type scf7tup is {scf7nat f}

function f(scf7tup x) => int:
    return x.f

method main(System.Console sys) => void:
    x = {f: -1}
    x.f = x.f + 1
    f(x)

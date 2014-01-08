
type scf5nat is int where $ > 0

function f({scf5nat f} x) => int:
    return x.f

method main(System.Console sys) => void:
    x = {f: 1}
    x.f = -1
    f(x)

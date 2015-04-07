
type scf9nat is (int n) where n > 0

type scf9tup is {scf9nat f, int g} where g > f

type scf9arr is ([{scf9nat f, int g}] ls) where some { z in ls | z.f == 1 }

function f(scf9arr xs) -> int:
    return |xs|

method main() -> void:
    [{int f, int g}] x = [{f: 1, g: 2}, {f: 4, g: 8}]
    x[0].f = 2
    f(x)

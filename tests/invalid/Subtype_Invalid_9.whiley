
type scf9nat is (int n) where n > 0
type scf9tup is ({scf9nat f, int g} r) where r.g > r.f
type scf9arr is ({scf9nat f, int g}[] ls) where some { i in 0..|ls| | ls[i].f == 1 }

function f(scf9arr xs) -> int:
    return |xs|

method main() :
    {int f, int g}[] x = [{f: 1, g: 2}, {f: 4, g: 8}]
    x[0].f = 2
    f(x)

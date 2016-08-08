
type scf8nat is (int n) where n > 0

type scf8tup is ({scf8nat f, int g} r) where r.g > r.f

function f(scf8tup[] xs) -> int:
    return |xs|

method main() :
    {int f, int g}[] x = [{f: 1, g: 2}, {f: 4, g: 8}]
    x[0].f = 2
    f(x)

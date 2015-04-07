
type scf6nat is (int n) where n > 0

type scf6tup is {scf6nat f, int g} where g > f

function f(scf6tup x) -> int:
    return x.f

method main() -> void:
    {int f, int g} x = {f: 1, g: 2}
    x.f = 2
    f(x)


type scf5nat is (int n) where n > 0

function f({scf5nat f} x) -> int:
    return x.f

method main() :
    {int f} x = {f: 1}
    x.f = -1
    f(x)


type scf5nat is (int n) where n > 0

function f({scf5nat f} x) -> int:
    return x.f

public export method test() :
    {int f} x = {f: 1}
    x.f = -1
    f(({scf5nat f}) x)

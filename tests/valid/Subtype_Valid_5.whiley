

type sr9nat is (int x) where x > 0

type sr9tup is ({sr9nat f, int g} r) where r.g > r.f

type sr9arr is (sr9tup[] xs) where some { i in 0..|xs| | xs[i].f == 1 }

public export method test() :
    sr9arr x = [{f: 1, g: 2}, {f: 1, g: 8}]
    x[0].f = 2
    assume x == [{f: 2, g: 2}, {f: 1, g: 8}]

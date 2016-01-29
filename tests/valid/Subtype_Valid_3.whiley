type sr8nat is (int n) where n > 0

type sr8tup is ({sr8nat f, int g} r) where r.g > r.f

public export method test() :
    sr8tup[] x = [{f: 1, g: 3}, {f: 4, g: 8}]
    x[0].f = 2
    assume x == [{f: 2, g: 3}, {f: 4, g: 8}]

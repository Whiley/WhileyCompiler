import whiley.lang.*

type sr9nat is (int x) where x > 0

type sr9tup is {sr9nat f, int g} where g > f

type sr9arr is ([sr9tup] xs) where some { z in xs | z.f == 1 }

method main(System.Console sys) -> void:
    sr9arr x = [{f: 1, g: 2}, {f: 1, g: 8}]
    x[0].f = 2
    sys.out.println(x)

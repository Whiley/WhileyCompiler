import whiley.lang.*

type sr9nat is int

type sr9tup is {sr9nat f, int g}

type sr9arr is [{sr9nat f, int g}]

method main(System.Console sys) -> void:
    sr9arr x = [{f: 1, g: 2}, {f: 1, g: 8}]
    x[0].f = 2
    sys.out.println(x)

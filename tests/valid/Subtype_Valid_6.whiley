type sr9nat is int

type sr9tup is {sr9nat f, int g}

type sr9arr is {sr9nat f, int g}[]

public export method test() :
    sr9arr x = [{f: 1, g: 2}, {f: 1, g: 8}]
    x[0].f = 2
    assume x == [{f: 2, g: 2}, {f: 1, g: 8}]


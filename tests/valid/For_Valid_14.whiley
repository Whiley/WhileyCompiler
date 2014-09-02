import whiley.lang.System

type liststr is [int] | string

function f(liststr ls) => int:
    int r = 0
    for l in ls:
        r = r + ((int)l)
    return r

method main(System.Console sys) => void:
    [int] ls = [1, 2, 3, 4, 5, 6, 7, 8]
    sys.out.println(f(ls))
    string xs = "Hello World"
    sys.out.println(f(xs))

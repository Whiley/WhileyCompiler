import whiley.lang.System

type liststr is [int] | string

function f(liststr ls) => int:
    int r = 0
    for l in ls:
        r = r + l
    return r

method main(System.Console sys) => void:
    ls = [1, 2, 3, 4, 5, 6, 7, 8]
    sys.out.println(f(ls))
    ls = "Hello World"
    sys.out.println(f(ls))

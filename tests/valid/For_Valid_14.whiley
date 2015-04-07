import whiley.lang.*

type list is [int]

function f(list ls) -> int:
    int r = 0
    for l in ls:
        r = r + ((int)l)
    return r

method main(System.Console sys) -> void:
    [int] ls = [1, 2, 3, 4, 5, 6, 7, 8]
    sys.out.println(f(ls))
    [int] xs = "Hello World"
    sys.out.println(f(xs))

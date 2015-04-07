import whiley.lang.*

type listset is [int] | {int}

function f(listset ls) -> int:
    int r = 0
    for l in ls:
        r = r + l
    return r

method main(System.Console sys) -> void:
    [int] ls = [1, 2, 3, 4, 5]
    sys.out.println(f(ls))
    {int} ss = {10, 20, 30, 40, 50}
    sys.out.println(f(ss))

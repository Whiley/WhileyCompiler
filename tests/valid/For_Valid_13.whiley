import whiley.lang.*

type listsetdict is [int] | {int} | {int=>int}

function f(listsetdict ls) -> int:
    int r = 0
    for l in ls:
        r = r + 1
    return r

method main(System.Console sys) -> void:
    {int} ls = {1, 2, 3, 4, 5}
    sys.out.println(f(ls))
    [int] xs = [1, 2, 3, 4, 5, 6, 7, 8]
    sys.out.println(f(xs))
    {int=>int} ms = {10=>20, 30=>40}
    sys.out.println(f(ms))

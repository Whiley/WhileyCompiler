import println from whiley.lang.System

type listset is [int] | {int}

function f(listset ls) => int:
    r = 0
    for l in ls:
        r = r + l
    return r

method main(System.Console sys) => void:
    ls = [1, 2, 3, 4, 5]
    sys.out.println(f(ls))
    ls = {10, 20, 30, 40, 50}
    sys.out.println(f(ls))

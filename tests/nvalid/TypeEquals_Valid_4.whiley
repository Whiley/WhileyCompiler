import println from whiley.lang.System

type nlist is int | [int]

function f(int i, [nlist] xs) => int:
    if (i < 0) || (i >= |xs|):
        return 0
    else:
        if xs[i] is int:
            return xs[i]
        else:
            return 0

method main(System.Console sys) => void:
    x = f(2, [2, 3, 4])
    sys.out.println(Any.toString(x))

import whiley.lang.System

type posintlist is [int]

function sum(posintlist ls, int i) => int:
    if i == |ls|:
        return 0
    else:
        return ls[i] + sum(ls, i + 1)

function sum(posintlist ls) => int:
    return sum(ls, 0)

method main(System.Console sys) => void:
    c = sum([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(Any.toString(c))

import println from whiley.lang.System

type posintlist is [int] where no { x in $ | x < 0 }

function sum(posintlist ls, int i) => int
requires (i >= 0) && (i <= |ls|)
ensures $ >= 0:
    if i == |ls|:
        return 0
    else:
        return ls[i] + sum(ls, i + 1)

function sum(posintlist ls) => int
ensures $ >= 0:
    return sum(ls, 0)

method main(System.Console sys) => void:
    c = sum([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(Any.toString(c))

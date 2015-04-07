import whiley.lang.*

type posintlist is [int]

function sum(posintlist ls) -> int:
    if |ls| == 0:
        return 0
    else:
        [int] rest = ls[1..|ls|]
        return ls[0] + sum(rest)

method main(System.Console sys):
    int c = sum([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(c)

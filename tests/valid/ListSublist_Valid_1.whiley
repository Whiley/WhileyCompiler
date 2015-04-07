import whiley.lang.*

type posintlist is ([int] xs) where no { x in xs | x < 0 }

function sum(posintlist ls) -> (int result)
// Result cannot be negative
ensures result >= 0:
    if |ls| == 0:
        return 0
    else:
        [int] rest = ls[1..|ls|]
        return ls[0] + sum(rest)

method main(System.Console sys) -> void:
    int c = sum([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(c)

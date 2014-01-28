import whiley.lang.System

type nat is (int x) where x >= 0

function tail([int] ls) => [nat]
requires (|ls| > 0) && all { i in 1 .. |ls| | ls[i] >= 0 }:
    return ls[1..|ls|]

method main(System.Console sys) => void:
    sys.out.println(tail([1, 2, 3, 4]))
    sys.out.println(tail([1]))

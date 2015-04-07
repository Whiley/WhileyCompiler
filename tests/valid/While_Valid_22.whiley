import whiley.lang.*

function max([int] xs) -> (int result)
// Input list cannot be empty
requires |xs| > 0
// Return must be element of input list
ensures result in xs
// No element of input list is larger than return
ensures no { x in xs | x > result }:
    //
    int r = xs[0]
    int i = 0
    while i < |xs| where (r in xs) && no { j in 0 .. i | xs[j] > r }:
        r = Math.max(r, xs[i])
        i = i + 1
    return r

method main(System.Console sys) -> void:
    sys.out.println(max([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]))
    sys.out.println(max([-8, 7, 9, 1, -1, 2, 5, 6, -200, 4]))
    sys.out.println(max([1]))

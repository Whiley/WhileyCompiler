import whiley.lang.*

function reverse([int] ls) -> ([int] result)
ensures |result| == |ls|:
    int i = |ls|
    [int] r = []
    while i > 0 where i <= |ls| && |r| == |ls| - i:
        i = i - 1
        r = r ++ [ls[i]]
    return r

method main(System.Console sys) -> void:
    [int] rs = reverse([1, 2, 3, 4, 5])
    sys.out.println(rs)

import whiley.lang.*

type nat is (int x) where x >= 0

function create(nat count, int value) -> ([int] result)
// Returned list must have count elements
ensures |result| == count:
    //
    [int] r = []
    int i = 0
    while i < count where i <= count && i == |r|:
        r = r ++ [value]
        i = i + 1
    return r

method main(System.Console sys) -> void:
    sys.out.println(create(3, 3))
    sys.out.println(create(2, 2))
    sys.out.println(create(2, 1))
    sys.out.println(create(1, 1))
    sys.out.println(create(0, 0))

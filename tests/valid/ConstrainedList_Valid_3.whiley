import whiley.lang.*

type nat is (int x) where x >= 0

function init(nat length, int value) -> ([int] result)
ensures (|result| == length) && all { i in result | i == value }:
    //
    int i = 0
    [int] data = []
    while i != length where i == |data| && all { d in data | d == value }:
        data = data ++ [value]
        i = i + 1
    //
    return data

method main(System.Console sys) -> void:
    for i in 0 .. 10:
        sys.out.println(init(i, i))

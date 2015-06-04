

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

public export method test() -> void:
    assume init(0,0) == []
    assume init(1,1) == [1]
    assume init(2,2) == [2,2]
    assume init(3,3) == [3,3,3]
    assume init(4,4) == [4,4,4,4]
    assume init(5,5) == [5,5,5,5,5]
    assume init(6,6) == [6,6,6,6,6,6]

import println from whiley.lang.System

type nat is (int x) where x >= 0

function init(nat length, int value) => [int]
ensures (|$| == length) && all { i in $ | i == value }:
    i = 0
    data = []
    while i != length where (i == |data|) && all { d in data | d == value }:
        data = data + [value]
        i = i + 1
    return data

method main(System.Console sys) => void:
    for i in 0 .. 10:
        sys.out.println(Any.toString(init(i, i)))

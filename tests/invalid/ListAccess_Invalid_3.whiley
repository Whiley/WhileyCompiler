import * from whiley.lang.*

type intList is int | [int]

type tup is {intList data, int mode}

function f([{int data, int mode}] x) -> [{int data, int mode}]:
    return x

method main(System.Console sys) -> void:
    [tup] tups = [{data: 1, mode: 0}, {data: [1, 2, 3], mode: 1}]
    tups[0].data = 1
    tups = f(tups)
    sys.out.println(Any.toString(tups))

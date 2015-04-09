import whiley.lang.*

type listset is [int] | {int}

function len(listset l) -> int:
    return |l|

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    assume len(l) == 3
    {int} s = {1, 2, 3, 4, 5, 6}
    assume len(s) == 6

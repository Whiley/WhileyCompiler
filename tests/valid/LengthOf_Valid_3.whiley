import whiley.lang.*

type setdict is {int} | {int=>int}

function len(setdict l) -> int:
    return |l|

method main(System.Console sys) -> void:
    {int} l = {1, 2, 3}
    assume len(l) == 3
    {int=>int} m = {1=>2, 3=>4, 5=>6, 7=>8}
    assume len(m) == 4

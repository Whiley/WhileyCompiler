

type listsetdict is [int] | {int} | {int=>int}

function len(listsetdict l) -> int:
    return |l|

public export method test() -> void:
    {int} s = {1, 2, 3}
    assume len(s) == 3
    [int] l = [1, 2]
    assume len(l) == 2
    {int=>int} m = {1=>2, 3=>4, 5=>6, 7=>8}
    assume len(m) == 4

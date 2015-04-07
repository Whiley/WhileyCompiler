import whiley.lang.*

type listsetdict is [int] | {int} | {int=>int}

function len(listsetdict l) -> int:
    return |l|

method main(System.Console sys) -> void:
    {int} s = {1, 2, 3}
    sys.out.println(len(s))
    [int] l = [1, 2]
    sys.out.println(len(l))
    {int=>int} m = {1=>2, 3=>4, 5=>6, 7=>8}
    sys.out.println(len(m))

import whiley.lang.*

type listdict is [int] | {int=>int}

function len(listdict l) -> int:
    return |l|

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    sys.out.println(len(l))
    {int=>int} m = {1=>2, 3=>4, 5=>6, 7=>8}
    sys.out.println(len(m))

import whiley.lang.*

type list is [int]

function len(list l) -> int:
    return |l|

method main(System.Console sys) -> void:
    [int] l = [1, 2]
    sys.out.println(len(l))
    [int] s = "Hello World"
    sys.out.println(len(s))

import whiley.lang.System

type liststr is [int] | ASCII.string

function len(liststr l) -> int:
    return |l|

method main(System.Console sys) -> void:
    [int] l = [1, 2]
    sys.out.println(len(l))
    ASCII.string s = "Hello World"
    sys.out.println(len(s))

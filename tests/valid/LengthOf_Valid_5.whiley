import println from whiley.lang.System

type liststr is [int] | string

function len(liststr l) => int:
    return |l|

method main(System.Console sys) => void:
    l = [1, 2]
    sys.out.println(len(l))
    l = "Hello World"
    sys.out.println(len(l))

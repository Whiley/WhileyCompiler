import whiley.lang.System

type liststr is [int] | string

function len(liststr l) => int:
    return |l|

method main(System.Console sys) => void:
    [int] l = [1, 2]
    sys.out.println(len(l))
    string s = "Hello World"
    sys.out.println(len(s))

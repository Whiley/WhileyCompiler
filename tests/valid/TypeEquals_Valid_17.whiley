import whiley.lang.System

type rlist is real | [int]

function f(rlist l) => int:
    if l is real:
        return 0
    else:
        return |l|

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(123.0)))
    sys.out.println(Any.toString(f(1.23)))
    sys.out.println(Any.toString(f([1, 2, 3])))

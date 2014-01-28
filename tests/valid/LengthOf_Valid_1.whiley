import whiley.lang.System

type listset is [int] | {int}

function len(listset l) => int:
    return |l|

method main(System.Console sys) => void:
    l = [1, 2, 3]
    sys.out.println(len(l))
    l = {1, 2, 3, 4, 5, 6}
    sys.out.println(len(l))

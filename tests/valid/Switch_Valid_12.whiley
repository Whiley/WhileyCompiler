import whiley.lang.System

function f([int] x) => int:
    switch x:
        case []:
            return 0
        case [1]:
            return -1
    return 10

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f([])))
    sys.out.println(Any.toString(f([1])))
    sys.out.println(Any.toString(f([3])))
    sys.out.println(Any.toString(f([1, 2, 3])))

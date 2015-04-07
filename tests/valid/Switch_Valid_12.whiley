import whiley.lang.*

function f([int] x) -> int:
    switch x:
        case []:
            return 0
        case [1]:
            return -1
    return 10

method main(System.Console sys) -> void:
    sys.out.println(f([]))
    sys.out.println(f([1]))
    sys.out.println(f([3]))
    sys.out.println(f([1, 2, 3]))

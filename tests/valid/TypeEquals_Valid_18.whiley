import whiley.lang.*

function f([int | real] e) -> [int]:
    if e is [int]:
        return e
    else:
        return [1, 2, 3]

method main(System.Console sys) -> void:
    sys.out.println(f([1, 2, 3, 4, 5, 6, 7]))
    sys.out.println(f([]))
    sys.out.println(f([1, 2, 2.01]))
    sys.out.println(f([1.23, 2, 2.01]))

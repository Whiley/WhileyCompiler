import whiley.lang.*

type src is int | [int] | [[int]]

function f(src e) -> bool:
    if e is [any]:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f([[1], [2]]))
    sys.out.println(f(1))

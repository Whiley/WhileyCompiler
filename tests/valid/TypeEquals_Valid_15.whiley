import whiley.lang.System

type src is int | [int] | [[int]]

function f(src e) => string:
    if e is [any]:
        return "[*]"
    else:
        return "int"

method main(System.Console sys) => void:
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f([[1], [2]]))
    sys.out.println(f(1))

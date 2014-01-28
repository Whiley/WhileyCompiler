import whiley.lang.System

type src is int | [src]

function f(src e) => string:
    if e is [any]:
        return "[*]"
    else:
        return "int"

method main(System.Console sys) => void:
    sys.out.println(f([1]))
    sys.out.println(f([[1]]))
    sys.out.println(f([[[1]]]))
    sys.out.println(f(1))

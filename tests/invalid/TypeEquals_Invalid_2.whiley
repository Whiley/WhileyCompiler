import * from whiley.lang.*

type ilist is real | [int]

function f(real e) -> string:
    if e is real:
        return "real"
    else:
        if e is int:
            return "int"
        else:
            return "[int]"

method main(System.Console sys) -> void:
    sys.out.println(f(1))

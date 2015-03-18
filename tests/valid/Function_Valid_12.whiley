import whiley.lang.System

type msg is {int op, int s}

function f(msg m) -> ASCII.string:
    return Any.toString(m)

function f([int] ls) -> ASCII.string:
    return Any.toString(ls)

function f([real] ls) -> ASCII.string:
    return Any.toString(ls)

method main(System.Console sys) -> void:
    sys.out.println_s(f([1, 2, 3]))
    sys.out.println_s(f([1.2, 2.2, 3.3]))

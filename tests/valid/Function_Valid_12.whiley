import whiley.lang.System

type msg is {int op, int s}

function f(msg m) => string:
    return Any.toString(m)

function f([int] ls) => string:
    return Any.toString(ls)

function f([real] ls) => string:
    return Any.toString(ls)

method main(System.Console sys) => void:
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f([1.2, 2.2, 3.3]))

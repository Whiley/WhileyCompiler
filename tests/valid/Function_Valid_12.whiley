import whiley.lang.*

type msg is {int op, int s}

function f(msg m) -> int:
    return m.op + m.s

function f([int] ls) -> [int]:
    return ls

function f([real] ls) -> [real]:
    return ls

method main(System.Console sys) -> void:
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f([1.2, 2.2, 3.3]))

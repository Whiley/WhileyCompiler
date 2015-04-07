import whiley.lang.*

type msg1 is {int op, int s} where op == 1

type msg2 is {int op, int s} where op == 2

function f(msg1 m) -> int:
    return 1

function f(msg2 m) -> int:
    return 2

method main(System.Console sys) -> void:
    msg1 m1 = {op: 1, s: 123}
    msg1 m2 = {op: 2, s: 123}
    sys.out.println(f(m1))
    sys.out.println(f(m2))

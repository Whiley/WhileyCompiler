import println from whiley.lang.System

type msg1 is {int op, int s} where op == 1

type msg2 is {int op, int s} where op == 2

function f(msg1 m) => string:
    return "MSG1"

function f(msg2 m) => string:
    return "MSG2"

method main(System.Console sys) => void:
    m1 = {op: 1, s: 123}
    m2 = {op: 2, s: 123}
    sys.out.println(f(m1))
    sys.out.println(f(m2))

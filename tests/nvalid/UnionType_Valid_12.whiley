import println from whiley.lang.System

type IntList is {int op, [real] rest} | {int op, int mode}

function f(IntList y) => string:
    return Any.toString(y)

function g({int op, int mode} z) => string:
    return Any.toString(z)

method main(System.Console sys) => void:
    x = {op: 1, rest: [1.23]}
    sys.out.println(f(x))
    x = {op: 1.23, mode: 0}
    x.op = 123
    sys.out.println(g(x))

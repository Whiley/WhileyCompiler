import println from whiley.lang.System

type IntList is {int op, [real] rest} | {int op, int mode}

function f(IntList y) => string:
    return Any.toString(y)

method main(System.Console sys) => void:
    x = {op: 1, rest: [1.23]}
    if |sys.args| == 10:
        x = {op: 1.23, mode: 0}
    x.op = 123
    sys.out.println(f(x))

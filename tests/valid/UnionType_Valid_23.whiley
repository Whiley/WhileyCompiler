import whiley.lang.*

type IntList is {int op, [real] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

method main(System.Console sys) -> void:
    {int|real op, [real] rest} x = {op: 1, rest: [1.23]}
    if |sys.args| == 10:
        x = {op: 1.23, rest: [1.0]}
    x.op = 123
    sys.out.println(f(x))

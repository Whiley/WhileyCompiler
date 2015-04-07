import whiley.lang.*

type IntList is {int op, [real] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

function g({int op, int mode} z) -> IntList:
    return z

method main(System.Console sys) -> void:
    IntList x = {op: 1, rest: [1.23]}
    sys.out.println(f(x))
    x = {op: 123, mode: 0}
    sys.out.println(g(x))

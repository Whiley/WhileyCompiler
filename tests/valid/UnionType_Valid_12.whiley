

type IntList is {int op, real[] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

function g({int op, int mode} z) -> IntList:
    return z

public export method test() :
    IntList x = {op: 1, rest: [1.23]}
    assume f(x) == {op: 1, rest: [1.23]}
    x = {op: 123, mode: 0}
    assume g(x) == {op: 123, mode: 0}



type IntList is {int op, real[] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

public export method test() :
    {int|real op, real[] rest} x = {op: 1, rest: [1.23]}
    if 0 == 10:
        x = {op: 1.23, rest: [1.0]}
    x.op = 123
    assume f(x) == {op: 123, rest: [1.23]}

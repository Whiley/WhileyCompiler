

type IntList is {int op, bool[] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

public export method test() :
    {int|bool op, bool[] rest} x = {op: 1, rest: [false]}
    if 0 == 10:
        x = {op: false, rest: [false]}
    x.op = 123
    assume f(x) == {op: 123, rest: [false]}

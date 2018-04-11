
type IntList is {int op, bool[] rest} | {int op, int mode}

function f(IntList y) -> IntList:
    return y

public export method test() :
    IntList x = {op: 1, rest: [false]}
    if 0 == 10:
        x = {op: 0, rest: [false]}
    if x is {int op, bool[] rest}:
        x.op = 123
    else:
        x.op = 123
    assume f(x) == {op: 123, rest: [false]}

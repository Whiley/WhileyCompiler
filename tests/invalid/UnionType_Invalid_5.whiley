type IntList is {int op, bool[] rest} | {int[] op, int mode}

function f(IntList x) -> IntList:
    return x

public export method test() :
    IntList x = {op: 1, rest: [false]}
    x.op = [1, 2, 3]
    f(x)

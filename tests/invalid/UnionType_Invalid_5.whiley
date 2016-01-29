type IntList is {int op, bool[] rest} | {int[] op, int mode}

function f(IntList x) -> IntList:
    return x

method main() :
    IntList x = {op: 1, rest: [false]}
    x.op = [1, 2, 3]
    f(x)

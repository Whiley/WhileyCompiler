type IntList is {int | int[] op}

function f({int op} x) -> int:
    return x.op

method main():
    IntList x = {op: 1}
    x.op = 2
    x.op = [1, 2, 3]
    f(x)

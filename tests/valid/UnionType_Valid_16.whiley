

type IntList is {int | int[] op}

public export method test() :
    IntList x = {op: 2}
    x.op = 1
    IntList y = x
    assume y == {op: 1}
    x = {op: [1, 2, 3]}
    assume x == {op: [1,2,3]}

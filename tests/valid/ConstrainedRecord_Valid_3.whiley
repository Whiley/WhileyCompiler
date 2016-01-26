

type btup is {int index, int op}

function f(btup b) -> int[]:
    return [b.op, b.index]

public export method test() :
    assume f({index: 2, op: 1}) == [1,2]

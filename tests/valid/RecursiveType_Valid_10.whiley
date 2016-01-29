

type expr is int | {int op, expr left, expr right}

public export method test() :
    expr e = {op: 1, left: 1, right: 2}
    assert e.op == 1
    assert e.left == 1
    assert e.right == 2

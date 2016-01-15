

type binop is {int op, expr left, expr right}

type expr is int | binop

public export method test() :
    expr e = 123
    assert e == 123

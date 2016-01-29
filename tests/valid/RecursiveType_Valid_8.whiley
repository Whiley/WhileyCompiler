

type nat is (int n) where n >= 0

type expr is nat | {int op, expr left, expr right}

public export method test() :
    expr e = 14897
    assert e == 14897

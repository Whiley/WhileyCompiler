

type Expr is int | {int op, Expr left, Expr right}

public export method test() :
    Expr e = 1
    assume e == 1

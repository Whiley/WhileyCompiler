

constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, Expr left, Expr right}

type asbinop is {int op, Expr left, Expr right}

type Expr is int | binop

public export method test() :
    Expr bop1 = {op: ADD, left: 1, right: 2}
    Expr bop2 = bop1
    Expr e1 = bop1
    Expr e2 = {op: SUB, left: bop1, right: 2}
    assume e1 == {left:1,op:1,right:2}
    assume e2 == {left:{left:1,op:1,right:2},op:2,right:2}

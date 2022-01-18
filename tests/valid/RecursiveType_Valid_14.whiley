final int ADD = 1
final int SUB = 2
final int MUL = 3
final int DIV = 4

type binop is ({int op, Expr left, Expr right} _this) where _this.op == ADD || _this.op ==  SUB || _this.op ==  MUL || _this.op ==  DIV

type asbinop is ({int op, Expr left, Expr right} _this) where _this.op == ADD || _this.op ==  SUB

type Expr is int | binop

public export method test() :
    Expr bop1 = {op: ADD, left: 1, right: 2}
    Expr bop2 = bop1
    Expr e1 = bop1
    Expr e2 = {op: SUB, left: bop1, right: 2}
    assume e1 == {left:1,op:1,right:2}
    assume e2 == {left:{left:1,op:1,right:2},op:2,right:2}


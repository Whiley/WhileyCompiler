int ADD = 1
int SUB = 2
int MUL = 3
int DIV = 4

type binop is ({int op, expr left, expr right} _this)
where _this.op == ADD || _this.op ==  SUB || _this.op ==  MUL || _this.op ==  DIV

type expr is int | binop

public export method test() :
    expr e1 = {op: ADD, left: 1, right: 2}
    expr e2 = {op: SUB, left: e1, right: 2}
    expr e3 = {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}
    assert e1 == {op: ADD, left: 1, right: 2}
    assert e2 == {op: SUB, left: {op: ADD, left: 1, right: 2}, right: 2}
    assert e3 == {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}

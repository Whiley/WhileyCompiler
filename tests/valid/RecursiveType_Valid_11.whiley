

constant ADD is 1
constant SUB is 2
constant MUL is 3
constant DIV is 4

type binop is ({int op, expr left, expr right} this)
where this.op == ADD || this.op ==  SUB || this.op ==  MUL || this.op ==  DIV

type expr is int | binop

public export method test() :
    expr e1 = {op: ADD, left: 1, right: 2}
    expr e2 = {op: SUB, left: e1, right: 2}
    expr e3 = {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}
    assert e1 == {op: ADD, left: 1, right: 2}
    assert e2 == {op: SUB, left: {op: ADD, left: 1, right: 2}, right: 2}
    assert e3 == {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}

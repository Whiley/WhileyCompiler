

constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, expr left, expr right}

type expr is int | binop

public export method test() :
    expr e1 = {op: ADD, left: 1, right: 2}
    expr e2 = {op: SUB, left: e1, right: 2}
    expr e3 = {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}
    assume e1 == {left:1,op:1,right:2}
    assume e2 == {left:{left:1,op:1,right:2},op:2,right:2}
    assume e3 == {left:{left:2,op:3,right:2},op:2,right:2}




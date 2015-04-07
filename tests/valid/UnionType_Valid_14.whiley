import whiley.lang.*

constant ADD is 0

constant SUB is 1

constant MUL is 2

constant DIV is 3

type BOp is (int x) where x in {ADD, SUB, MUL, DIV}

type BinOp is {BOp op, Expr rhs, Expr lhs}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | [Expr] | ListAccess

function f(Expr e) -> int:
    if e is int:
        return e
    else:
        if e is [int]:
            return |e|
        else:
            return 1

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f({op: ADD, rhs: 2, lhs: 1}))

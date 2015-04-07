import whiley.lang.*
import SyntaxError from whiley.lang.*

constant ADD is 0

constant SUB is 1

constant MUL is 2

constant DIV is 3

type BOp is (int x) where x in {ADD, SUB, MUL, DIV}

type BinOp is {BOp op, Expr rhs, Expr lhs}

type Var is {[int] id}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | [Expr] | ListAccess

type Value is int | [Value]

function evaluate(Expr e) -> Value:
    if e is int:
        return e
    else:
        if e is BinOp:
            return evaluate(e.lhs)
        else:
            if e is [Expr]:
                return []
            else:
                if e is ListAccess:
                    Value src = evaluate(e.src)
                    Value index = evaluate(e.index)
                    if (src is [Value]) && ((index is int) && ((index >= 0) && (index < |src|))):
                        return src[index]
                    else:
                        return 0
                else:
                    return 0

method main(System.Console sys) -> void:
    Expr e = {op: ADD, rhs: 1, lhs: 123}
    Value v = evaluate(e)
    sys.out.println_s("RESULT: " ++ Any.toString(v))
    e = [1]
    v = evaluate(e)
    sys.out.println_s("RESULT: " ++ Any.toString(v))

import whiley.lang.*

type Expr is int | real | [Expr] | ListAccess

type ListAccess is {Expr index, Expr src}

type Value is int | real | [Value]

function evaluate(Expr e) -> null | Value:
    if (e is real) || (e is int):
        return e
    else:
        if e is [Expr]:
            [Value] r = []
            for i in e:
                null|Value v = evaluate(i)
                if v is null:
                    return v
                else:
                    r = r ++ [v]
            return r
        else:
            null|Value src = evaluate(e.src)
            null|Value index = evaluate(e.index)
            if src is [Expr] && index is int && index >= 0 && index < |src|:
                return src[index]
            else:
                return null

public method main(System.Console sys) -> void:
    sys.out.println(evaluate(123))
    sys.out.println(evaluate({index: 0, src: [112, 212332, 342]}))
    sys.out.println(evaluate({index: 2, src: [112312, -289712, 312242]}))
    sys.out.println(evaluate([123, 223, 323]))

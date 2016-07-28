type Expr is int | Expr[] | ListAccess

type ListAccess is {Expr index, Expr src}

type Value is int | Value[]

function evaluate(Expr e) -> null | Value:
    if (e is int):
        return e
    else:
        if e is Expr[]:
            Value[] r = [0;|e|]
            int i = 0
            while i < |e|:
                null|Value v = evaluate(e[i])
                if v is null:
                    return v
                else:
                    r[i] = v
                i = i + 1
            return r
        else:
            null|Value src = evaluate(e.src)
            null|Value index = evaluate(e.index)
            if src is Expr[] && index is int && index >= 0 && index < |src|:
                return src[index]
            else:
                return null

public export method test() :
    assume evaluate(123) == 123
    assume evaluate({index: 0, src: [112, 212332, 342]}) == 112
    assume evaluate({index: 2, src: [112312, -289712, 312242]}) == 312242
    assume evaluate([123, 223, 323]) == [123, 223, 323]


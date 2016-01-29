

type Expr is int | real | Expr[] | ListAccess

type ListAccess is {Expr index, Expr src}

type Value is int | real | Value[] | null

function evaluate(Expr e) -> Value:
    if (e is real) || (e is int):
        return e
    else:
        if e is Expr[]:
            Value[] r = []
            for i in e:
                r = r ++ [evaluate(i)]
            return r
        else:
            Value src = evaluate(e.src)
            Value index = evaluate(e.index)
            if (src is Expr[] && index is int &&
                    index >= 0 && index < |src|):
                return src[index]
            else:
                return null

public export method test() :
    assume evaluate(1) == 1
    assume evaluate({index: 0, src: [112, 212, 342]}) == 112
    assume evaluate({index: 2, src: [112312, 289712, 31231242]}) == 31231242
    assume evaluate([1, 2, 3]) == [1, 2, 3]


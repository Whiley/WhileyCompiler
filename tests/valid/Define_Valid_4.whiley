import whiley.lang.System

constant codeOp is {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(codeOp x) => string:
    code y = {op: x, payload: []}
    return Any.toString(y)

method main(System.Console sys) => void:
    sys.out.println(f(1))

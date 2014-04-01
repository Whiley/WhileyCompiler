import whiley.lang.System

constant codeOp is {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(code x) => string:
    int y = x.op
    return Any.toString(y)

method main(System.Console sys) => void:
    sys.out.println(f({op: 1, payload: [1]}))

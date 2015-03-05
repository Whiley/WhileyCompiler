import whiley.lang.System

type codeOp is (int x) where x in {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(code x) -> ASCII.string:
    int y = x.op
    return Any.toString(y)

method main(System.Console sys) -> void:
    sys.out.println(f({op: 1, payload: [1]}))

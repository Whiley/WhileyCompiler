import whiley.lang.*

type codeOp is (int x) where x in {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(code x) -> int:
    int y = x.op
    return y

method main(System.Console sys) -> void:
    assume f({op: 1, payload: [1]}) == 1

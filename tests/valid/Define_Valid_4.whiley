

type codeOp is (int x) where x in {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(codeOp x) -> code:
    code y = {op: x, payload: []}
    return y

public export method test() -> void:
    assume f(1) == {op:1, payload: []}

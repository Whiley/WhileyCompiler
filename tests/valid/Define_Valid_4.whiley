

type codeOp is (int x) where 1 <= x && x <= 4

type code is {codeOp op, int[] payload}

function f(codeOp x) -> code:
    code y = {op: x, payload: [0]}
    return y

public export method test() :
    assume f(1) == {op:1, payload: [0]}

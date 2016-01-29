type codeOp is (int x) where 1 <= x && x <= 4

type code is {codeOp op, int[] payload}

function f(code x) -> int: 
    int y = x.op
    return y

public export method test() :
    assume f({op: 1, payload: [1]}) == 1

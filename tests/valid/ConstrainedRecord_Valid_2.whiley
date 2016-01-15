

constant RET is 169

constant NOP is 0

type unitCode is (int x) where x == NOP || x == RET

type UNIT is {unitCode op}

function f(UNIT x) -> int:
    return x.op

public export method test() :
    int bytes = f({op: NOP})
    assume bytes == NOP

final int RET = 169
final int NOP = 0

type unitCode is (int x) where x == NOP || x == RET

type UNIT is {unitCode op}

function f(UNIT x) -> int[]:
    return [x.op]

public export method test() :
    int[] bytes = f({op: (unitCode) NOP})
    assume bytes == [NOP]

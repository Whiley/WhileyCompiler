import whiley.lang.System

constant RET is 169

constant NOP is 0

constant unitCode is {NOP, RET}

type UNIT is {unitCode op}

function f(UNIT x) => [int]:
    return [x.op]

method main(System.Console sys) => void:
    [int] bytes = f({op: NOP})
    sys.out.println(Any.toString(bytes))

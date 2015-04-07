import whiley.lang.*

constant RET is 169

constant NOP is 0

type unitCode is (int x) where x in {NOP, RET}

type UNIT is {unitCode op}

function f(UNIT x) -> [int]:
    return [x.op]

method main(System.Console sys) -> void:
    [int] bytes = f({op: NOP})
    sys.out.println(bytes)

import println from whiley.lang.System

define RET as 169
define NOP as 0

define unitCode as { NOP, RET }
define UNIT as {unitCode op}

int f(UNIT x):
    return x.op

void ::main(System.Console sys):
    bytes = f({op:NOP})
    sys.out.println(Any.toString(bytes))


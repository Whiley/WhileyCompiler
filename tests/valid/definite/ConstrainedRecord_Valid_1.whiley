define RET as 169
define NOP as 0

define unitCode as { NOP, RET }
define UNIT as {unitCode op}

[byte] f(UNIT x):
    return [x.op]

void System::main([string] args):
    bytes = f({op:NOP})
    out->println(str(bytes))


define 169 as RET
define 0 as NOP

define { NOP, RET } as unitCode
define (unitCode op) as UNIT

byte f(UNIT x):
    return x.op

void System::main([string] args):
    byte bytes = f((op:NOP))
    print str(bytes)


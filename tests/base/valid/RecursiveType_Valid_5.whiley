define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as {int op, expr left, expr right}
define expr as int | binop

void System::main([string] args):
    e1 = {op:ADD,left:1,right:2}
    e2 = {op:SUB,left:e1,right:2}
    e3 = {op:SUB,left:{op:MUL,left:2,right:2},right:2}
    this.out.println(str(e1))
    this.out.println(str(e2))
    this.out.println(str(e3))

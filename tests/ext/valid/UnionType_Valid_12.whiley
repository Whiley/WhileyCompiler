define utr12nat as int where $ >= 0
define intList as utr12nat|[int]

define tupper as {int op, intList il} where op >= 0 && op <= 5

int f(tupper y) ensures $ >= 0:
    return y.op

void System::main([string] args):
    x = {op:1,il:1}
    this.out.println(str(x))
    f(x)

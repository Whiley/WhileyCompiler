import * from whiley.lang.*

define IntList as {int op, [real] rest}|{int op, int mode}

string f(IntList y):
    return str(y)

string g({int op, int mode} z):
    return str(z)

void ::main(System sys,[string] args):
    x = {op:1, rest:[1.23]}
    sys.out.println(f(x))
    x = {op:1.23, mode: 0}
    x.op = 123 // OK
    sys.out.println(g(x))

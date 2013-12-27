import println from whiley.lang.System

define IntList as {int op, [real] rest}|{int op, int mode}

string f(IntList y):
    return Any.toString(y)

string g({int op, int mode} z):
    return Any.toString(z)

void ::main(System.Console sys):
    x = {op:1, rest:[1.23]}
    sys.out.println(f(x))
    x = {op:1.23, mode: 0}
    x.op = 123 // OK
    sys.out.println(g(x))

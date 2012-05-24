import println from whiley.lang.System

define msg as {int op, int s}

string f(msg m):
    return (Any.toString(m))

string f([int] ls):
    return (Any.toString(ls))

string f([real] ls):
    return (Any.toString(ls))

void ::main(System.Console sys):
    sys.out.println(f([1,2,3]))
    sys.out.println(f([1.2,2.2,3.3]))

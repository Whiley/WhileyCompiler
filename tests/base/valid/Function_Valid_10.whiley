import * from whiley.lang.*

define msg as {int op, int s}

string f(msg m):
    return (toString(m))

string f([int] ls):
    return (toString(ls))

string f([real] ls):
    return (toString(ls))

void ::main(System sys,[string] args):
    sys.out.println(f([1,2,3]))
    sys.out.println(f([1.2,2.2,3.3]))

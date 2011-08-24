import whiley.lang.*:*

define msg as {int op, int s}

string f(msg m):
    return (str(m))

string f([int] ls):
    return (str(ls))

string f([real] ls):
    return (str(ls))

void ::main(System sys,[string] args):
    sys.out.println(f([1,2,3]))
    sys.out.println(f([1.2,2.2,3.3]))

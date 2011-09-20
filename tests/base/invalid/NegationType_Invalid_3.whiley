import * from whiley.lang.*

define LinkedList as null|{LinkedList next, int data}

!(null|int) f(LinkedList x):
    return x

void ::main(System sys, [string] args):
    sys.out.println(String.str(f("Hello World")))

import * from whiley.lang.*

int f():
    x = spawn 1
    return 1

void ::main(System.Console sys,[string] args):
    x = f()
    sys.out.println(Any.toString(x))

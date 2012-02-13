import * from whiley.lang.*

int f():
    x = new 1
    return 1

void ::main(System.Console sys):
    x = f()
    sys.out.println(Any.toString(x))

import * from whiley.lang.*

void System::f(int x):
    sys.out.println(Any.toString(x))

void ::main(System.Console sys):
    f(1)

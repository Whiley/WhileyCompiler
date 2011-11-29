import * from whiley.lang.*

void f(int x):
    sys.out.println(toString(x))

void ::main(System sys,[string] args):
    this.f(1)

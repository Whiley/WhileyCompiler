import * from whiley.lang.*

void System::f(int x):
    sys.out.println(toString(x))

void ::main(System sys,[string] args):
    f(1)

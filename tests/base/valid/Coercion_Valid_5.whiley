import * from whiley.lang.*

{(int,real)} f({int->real} x):
    return x

void ::main(System sys,[string] args):
    x = f({1->2.2,2->3.3})
    sys.out.println(toString(x))
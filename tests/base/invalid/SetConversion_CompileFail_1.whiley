import * from whiley.lang.*

void f({real} ls):
    sys.out.println(Any.toString(ls))

void ::main(System.Console sys,[string] args):
    f({1,2,3,{}})

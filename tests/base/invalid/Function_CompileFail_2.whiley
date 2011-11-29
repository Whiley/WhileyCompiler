import * from whiley.lang.*

void f(int x):
    sys.out.println()"FIRST"

void f(int x):
    sys.out.println()"SECOND"

void ::main(System sys,[string] args):
    sys.out.println()"NOUT"
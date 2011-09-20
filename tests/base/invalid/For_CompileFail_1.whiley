import * from whiley.lang.*

void ::main(System sys,[string] args):
    st = "Hello World"
    for st in args:
        sys.out.println(st)

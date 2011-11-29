import * from whiley.lang.*

[int] f(string x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(toString(f("Hello World")))
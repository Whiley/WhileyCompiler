import * from whiley.lang.*

int f(char x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(toString(f('H')))
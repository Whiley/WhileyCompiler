import * from whiley.lang.*

void ::main(System sys,[string] args):
    s = "Hello World"
    s[0] = 1.234
    sys.out.println(s)

import * from whiley.lang.*

void ::main(System.Console sys):
    s = "Hello World"
    s[0] = 1.234
    sys.out.println(s)

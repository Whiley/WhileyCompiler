import * from whiley.lang.*

// this is a comment!
define IntReal as int | real

void f(int y):
    sys.out.println(str(y))

void ::main(System sys,[string] args):
    x = 123
    f(x)
    x = 1.234
    f(x)


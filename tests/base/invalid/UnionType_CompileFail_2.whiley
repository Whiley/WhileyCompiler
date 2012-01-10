

// this is a comment!
define IntReal as int | real

void f(int y):
    sys.out.println(Any.toString(y))

void ::main(System.Console sys):
    x = 123
    f(x)
    x = 1.234
    f(x)


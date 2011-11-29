import * from whiley.lang.*

// this is a comment!
define IntList as int|[int]

void System::f(int y):
    this.out.println(toString(y))

void System::g([int] z):
    this.out.println(toString(z))

void ::main(System sys,[string] args):
    x = 123
    sys.f(x)
    x = [1,2,3]
    sys.g(x)

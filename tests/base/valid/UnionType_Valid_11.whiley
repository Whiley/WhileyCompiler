import whiley.lang.*:*

// this is a comment!
define IntList as int|[int]

void System::f(int y):
    sys.out.println(str(y))

void System::g([int] z):
    sys.out.println(str(z))

void ::main(System sys,[string] args):
    x = 123
    this.f(x)
    x = [1,2,3]
    this.g(x)

import * from whiley.lang.*

// Tests that variables are maintained after synchronous message sends.
void ::main(System sys, [string] args):
    i = sys.var()
    sys.out!println(sys.self())
    sys.out!println(i)

int Console::self():
    return 1

int Console::var():
    return 2

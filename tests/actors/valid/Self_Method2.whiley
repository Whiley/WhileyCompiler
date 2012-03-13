import * from whiley.lang.*

void ::main(Console sys):
    sys.method()

// Tests that calling an internal method as an expression correctly yields.
void Console::method():
    this.out!println(this.self())

int Console::one():
    return 1

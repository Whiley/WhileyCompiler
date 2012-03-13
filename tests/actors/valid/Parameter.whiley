import * from whiley.lang.*

void ::main(Console sys):
    sys.method(2)

// Tests that reading a parameter doesn't affect the actor invariants.
void Console::method(int i):
    this.out!println(1)
    this.out!println(i)

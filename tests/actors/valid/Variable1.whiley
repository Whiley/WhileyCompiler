import * from whiley.lang.*

// Tests that an actor can correctly use the result of a synchronous message.
void ::main(Console sys):
    i = sys.var()
    sys.out!println(i)
    sys.out!println(i)

int Console::var():
    return 1

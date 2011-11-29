import toString from whiley.lang.Any
import * from whiley.lang.System

// Tests that an actor can correctly use the result of a synchronous message.
void ::main(System sys, [string] args):
    i = sys.var()
    sys.out!println(sys.self())
    sys.out!println(i)

string System::self():
    return toString(1)

string System::var():
    return toString(2)

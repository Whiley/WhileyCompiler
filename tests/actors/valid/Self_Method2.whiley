import toString from whiley.lang.Any
import * from whiley.lang.System

void ::main(System sys, [string] args):
    sys.out!println(sys.self())

string System::self():
    return toString(1)

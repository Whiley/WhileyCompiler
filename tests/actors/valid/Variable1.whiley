import toString from whiley.lang.Any
import * from whiley.lang.System

void ::main(System sys, [string] args):
    i = sys.var()
    sys.out!println(i)
    sys.out!println(i)

string System::var():
    return toString(1)

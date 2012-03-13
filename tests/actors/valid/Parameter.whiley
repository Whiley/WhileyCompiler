import toString from whiley.lang.Any
import * from whiley.lang.System

void ::main(System sys, [string] args):
    sys.self(2)

void System::self(int i):
    this.out!println(toString(1))
    this.out!println(toString(i))

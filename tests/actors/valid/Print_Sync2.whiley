import toString from whiley.lang.Any
import * from whiley.lang.System

void ::main(System sys, [string] args):
    sys.out.println(toString(6))
    sys.out.println(toString(5))

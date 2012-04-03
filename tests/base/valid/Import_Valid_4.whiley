import println from whiley.lang.System
import toString from whiley.lang.System

string f(int x):
    return toString(x)

import toString from whiley.lang.System

string g(real x):
    return toString(x)

public void ::main(System.Console sys):
    sys.out.println("FIRST: " + f(1))
    sys.out.println("SECOND: " + g(1.2344))

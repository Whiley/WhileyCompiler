import toString from whiley.lang.Int

string f(int x):
    return toString(x)

import toString from whiley.lang.Real

string g(real x):
    return toString(x)

public void ::main(System.Console sys):
    sys.out.println("FIRST: " + f(1))
    sys.out.println("SECOND: " + g(1.2344))

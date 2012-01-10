string f(int x):
    return toString(x)

// ordering of import statement is important in Whiley
import toString from whiley.lang.Any

string g(real x):
    return toString(x)

public void ::main(System.Console sys, [string] args):
    sys.out.println("FIRST: " + f(1))
    sys.out.println("SECOND: " + g(1.2344))

import * from whiley.lang.System
import Error from whiley.lang.Errors

int f(int x) throws Error:
    switch(x):
        case 0:
        case 1:
            return x+10
        case 2:
            return x+12
        default:
            throw Error("INVALID VALUE FOR X")

public void ::main(System.Console sys):
    try:
        sys.out.println(f(1))
        sys.out.println(f(2))
        sys.out.println(f(3))
    catch(Error e):
        sys.out.println(e.msg)
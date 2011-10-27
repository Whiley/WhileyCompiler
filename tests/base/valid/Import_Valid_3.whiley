import * from whiley.lang.System
import * from whiley.lang.String

int toInt([int] ls):
    r = 0
    for i in ls:
        r = r + i
    return r

public void ::main(System sys, [string] args):
    ls = [1,2,3,4]
    s = String.toString(toInt(ls))
    sys.out.println(s)

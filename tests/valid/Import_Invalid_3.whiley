import println from whiley.lang.System
import println from whiley.lang.System

int toInt([int] ls):
    r = 0
    for i in ls:
        r = r + i
    return r

public void ::main(System.Console sys):
    ls = [1,2,3,4]
    s = Any.toString(toInt(ls))
    sys.out.println(s)

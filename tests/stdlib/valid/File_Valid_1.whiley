// an exercise to test some native routines for I/O.
//

import println from whiley.lang.System
import * from whiley.io.File

int myint(byte b):
    r = 0
    base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r

char mychar(byte was):
    return (char) myint(was)

bool matching(string before, [byte] after):
    r = true
    for idx in 1..|before|:
        b = before[idx]
        a = after[idx]
        c = mychar(a)
        if b != c:
            r = false
    return r

void ::main(System.Console sys):
    x = "hello world"
    a = Writer("foo.output")
    a.println(x)
    a.close()
    b = Reader("foo.output")
    c = b.read()
    // sys.out.println(c)
    if matching(x, c):
        sys.out.println("OK")
    else:
        sys.out.println("not")


import * from whiley.lang.System

[int] f(string str):
    r = null
    for i in str:
        if r == null:
            r = [0]
        else:
            r = r + [r[0]]
    return r

public void ::main(System.Console sys):
    r = f("Hello")
    sys.out.println(r)
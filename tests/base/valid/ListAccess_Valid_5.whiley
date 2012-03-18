import * from whiley.lang.System

[int] f(string str):
    r = []
    for i in str:
        if r == null:
            r = [0]
        else if i == ' ':
            r = null
        else:
            r = r + [r[0]]
    return r

public void ::main(System.Console sys):
    r = f("Hello")
    sys.out.println(r)
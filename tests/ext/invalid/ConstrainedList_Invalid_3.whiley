import * from whiley.lang.*

public [char] update(string str):
    return [(char) -1]

public void f(char c):
    debug "" + c

public void ::main(System sys,[string] args):
    s1 = "Hello World"
    s1 = update(s1)
    if |s1| > 0:
        f(s1[0])

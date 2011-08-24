import whiley.lang.*:*

void f(int x) requires x >= 0:
    y = 10 / x
    debug str(x)
    debug str(y)

void ::main(System sys,[string] args):
    f(10)
    f(0)

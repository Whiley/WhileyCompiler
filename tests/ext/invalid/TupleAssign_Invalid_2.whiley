import whiley.lang.*:*

define tac2ta as {int f1, int f2} where f1 < f2
define tac2tb as {int f1, int f2} where (f1+1) < f2

tac2tb f(tac2tb y):
    return y

void ::main(System sys,[string] args):
    x = {f1:1, f2:3}
    debug str(x)
    x.f1 = 2
    debug str(f(x))

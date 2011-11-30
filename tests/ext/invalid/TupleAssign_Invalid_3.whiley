import * from whiley.lang.*

define tac3ta as {int f1, int f2} where f1 < f2

void ::main(System sys,[string] args):
    x = {f1:2, f2:3}
    y = {f1:1, f2:3}
    x.f1 = 1
    debug Any.toString(x)
    debug Any.toString(y)  
    assert x != y

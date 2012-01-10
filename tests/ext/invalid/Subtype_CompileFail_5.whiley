import * from whiley.lang.*

define scf5nat as int where $ > 0

int f({scf5nat f} x):
    return x.f

void ::main(System.Console sys,[string] args):
    x = {f:1}
    x.f = -1
    f(x)

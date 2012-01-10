import * from whiley.lang.*

real g(real x) requires x <= 0.5, ensures $ < 0.16:
     return x / 3.0

void ::main(System.Console sys,[string] args):
     debug Any.toString(g(0.234))
     debug Any.toString(g(0.5))

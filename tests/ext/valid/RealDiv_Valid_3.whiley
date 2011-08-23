import whiley.lang.*:*

real g(real x) requires x <= 0.5, ensures $ <= 0.166666666666668:
     return x / 3

void ::main(System sys,[string] args):
     sys.out.println(str(g(0.234)))

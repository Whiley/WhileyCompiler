import whiley.lang.*:*

real g(real x):
     return x / 3

void ::main(System sys,[string] args):
     sys.out.println(str(g(0.234)))

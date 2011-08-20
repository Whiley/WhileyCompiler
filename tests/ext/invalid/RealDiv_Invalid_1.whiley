import whiley.lang.*:*

real g(real x) requires x <= 0.5, ensures $ < 0.16:
     return x / 3.0

void System::main([string] args):
     debug str(g(0.234))
     debug str(g(0.5))

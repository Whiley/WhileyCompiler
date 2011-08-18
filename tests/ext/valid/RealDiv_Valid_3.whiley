real g(real x) requires x <= 0.5, ensures $ <= 0.166666666666668:
     return x / 3

void System::main([string] args):
     this.out.println(str(g(0.234)))

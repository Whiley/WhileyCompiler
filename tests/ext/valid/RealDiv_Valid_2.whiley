import whiley.lang.*:*

real g(int x):
     return x / 3

string f(int x, int y) requires x>=0 && y>0:
    return str(g(x))

void System::main([string] args):
     this.out.println(f(1,2))


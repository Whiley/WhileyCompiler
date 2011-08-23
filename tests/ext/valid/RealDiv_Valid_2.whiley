import whiley.lang.*:*

real g(int x):
     return x / 3

string f(int x, int y) requires x>=0 && y>0:
    return str(g(x))

void ::main(System sys,[string] args):
     sys.out.println(f(1,2))


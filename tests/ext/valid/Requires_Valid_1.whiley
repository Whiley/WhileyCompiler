import * from whiley.lang.*

int f(int x):
    return x+1

(string,string) g(int x, int y) requires y == f(x):
    return (toString(x),str(y))

void ::main(System sys,[string] args):
    (x,y) = g(1,f(1))
    debug x + "\n"
    debug y + "\n"

import println from whiley.lang.System

int f(int x):
    return x+1

(string,string) g(int x, int y) requires y == f(x):
    return Any.toString(x),toString(y)

void ::main(System.Console sys):
    (x,y) = g(1,f(1))
    debug x + "\n"
    debug y + "\n"

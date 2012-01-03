import * from whiley.lang.*

int f(int x) ensures $ > x:
    x = x + 1
    return x

void ::main(System sys,[string] args):
    y = f(1)
    sys.out.println(Any.toString(y))
    

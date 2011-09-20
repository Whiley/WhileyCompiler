import * from whiley.lang.*

int f1(int x):
    return x + 1

int f2(int x):
    return x * 2

int g(int(int) func):
    return func(1234)
    
void ::main(System sys,[string] args):
    sys.out.println(str(g(&f1)))
    sys.out.println(str(g(&f2)))
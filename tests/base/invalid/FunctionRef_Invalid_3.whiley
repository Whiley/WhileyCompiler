import whiley.lang.*:*

int f(int x):
    return x + 1

int g(int(real) func):
    return func(1.2345)
    
void ::main(System sys,[string] args):
    sys.out.println(str(g(&f)))
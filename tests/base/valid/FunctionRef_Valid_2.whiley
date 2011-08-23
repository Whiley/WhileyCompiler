import whiley.lang.*:*

real f(real x):
    return x + 1

real g(real(int) func):
    return func(1)
    
void ::main(System sys,[string] args):
    sys.out.println(str(g(&f)))
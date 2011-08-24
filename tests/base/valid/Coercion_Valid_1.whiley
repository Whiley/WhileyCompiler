import whiley.lang.*:*

real f(int x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(str(f(123)))
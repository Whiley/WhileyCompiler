import whiley.lang.*:*

int f(int x) requires x>=0, ensures $>=0 && x>=0:
    return x

void ::main(System sys,[string] args):
    sys.out.println(str(f(10)))

import println from whiley.lang.System

int add(int x, int y) requires x>=0 && y>=0, ensures $>0:
    if(x == y):
        return 1
    else:
        return x+y

void ::main(System.Console sys):
    sys.out.println(Any.toString(1))

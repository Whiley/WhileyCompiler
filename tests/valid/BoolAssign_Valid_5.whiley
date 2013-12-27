import println from whiley.lang.System

int f(int x, int y) requires x>=0 && y>=0, ensures $>0:
    a = true
    
    if(x < y):
        a = false
    
    if(!a):
        return x + y
    else:
        return 123

void ::main(System.Console sys):
    sys.out.println(Any.toString(1))

import * from whiley.lang.*

int f(int x, int y):
    a = true
    
    if(x < y):
        a = false
    
    if(!a):
        return x + y
    else:
        return 123

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(1))

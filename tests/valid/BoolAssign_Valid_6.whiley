import println from whiley.lang.System

int f(int x, int y):
    a = true
    
    if(x < y):
        a = false
    
    if(!a):
        return x + y
    else:
        return 123

void ::main(System.Console sys):
    sys.out.println(Any.toString(1))

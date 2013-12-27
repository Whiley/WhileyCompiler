import println from whiley.lang.System

int read(int a):
    return -a

int id(int x):
    return x

int test(int(int) read, int arg):    
    return read(arg)
    
void ::main(System.Console sys):
    x = test(&id,1)
    sys.out.println(Any.toString(x))
    x = test(&id,123)
    sys.out.println(Any.toString(x))
    x = test(&id,223)
    sys.out.println(Any.toString(x))

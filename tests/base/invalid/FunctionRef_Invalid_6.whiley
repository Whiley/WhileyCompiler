import * from whiley.lang.*

real read(real a):
    return -a

int id(int x):
    return x

real test(int(int) read, real arg):    
    return read(arg)
    
void ::main(System.Console sys,[string] args):
    x = test(&id,1)
    sys.out.println(Any.toString(x))
    x = test(&id,123)
    sys.out.println(Any.toString(x))
    x = test(&id,223)
    sys.out.println(Any.toString(x))
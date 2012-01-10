import * from whiley.lang.*

int f(int x) requires x >= 0:
    return x

int f(int x) requires x >= 0:
    return x

void ::main(System.Console sys):    
    debug Any.toString(f(1))
    
    

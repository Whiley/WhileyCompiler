import * from whiley.lang.*

int f(int x) requires x >= 0:
    return x

int f(int x) requires x >= 0:
    return x

void ::main(System sys,[string] args):    
    debug toString(f(1))
    
    

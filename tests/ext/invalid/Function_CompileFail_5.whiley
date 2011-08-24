import whiley.lang.*:*

int f(int x) requires x >= 0:
    return x

int f(int x) requires x >= 0:
    return x

void ::main(System sys,[string] args):    
    debug str(f(1))
    
    

import println from whiley.lang.System

import whiley.io.*

define table as [
    &f1,&f2
]

int f1(int x):
    return x

int f2(int x):
    return -x

int g(int d):
    y = table[d]
    return y(123)
    
void ::main(System.Console sys):    
    sys.out.println(Any.toString(g(0)))    
    sys.out.println(Any.toString(g(1)))

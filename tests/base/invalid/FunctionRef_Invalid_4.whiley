import whiley.io.*

define table as [
    &f1,&f2,null
]

int f1(int x):
    return x

int f2(int x):
    return -x

int g(int d):
    y = table[d]
    return y(123)
    
void System::main([string] args):    
    out->println(str(g(3)))    

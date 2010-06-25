// this is a comment!
define int where $ > 0 as nat
define {1,2,3,4} as num

void f(num x):
    num y
    y = x
    print str(y)

void g(int x, nat z) requires (x == 1 || x == 2) && z in {1,2,3,x}:
    f(z)

void System::main([string] args):
    g(1,3)

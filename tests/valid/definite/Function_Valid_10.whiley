define (int op, int s) as msg

void f(msg m):
    print str(m)

void f([int] ls):
    print str(ls)

void f([real] ls):
    print str(ls)

void System::main([string] args):
    f([1,2,3])
    f([1.2,2.2,3.3])

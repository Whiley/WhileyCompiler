void f(int|null x):
    if x ~= null:
        print "GOT NULL"
    else:
        print "GOT INT"

void System::main([string] args):
    x = null
    f(x)
    f(1)

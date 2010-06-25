define int where $ > 0 as posint
define int where $ < 0 as negint

int f(posint x) ensures $ < 0:
    print "F CALLED"
    return -x

void System::main([string] args):
    int x = |args|
    if(x > 0):
        f(x)        

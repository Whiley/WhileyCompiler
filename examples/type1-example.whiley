define posint as int where $ > 0
define negint as int where $ < 0

int f(posint x) ensures $ < 0:
    print "F CALLED"
    return -x

void System::main([string] args):
    x = |args|
    if(x > 0):
        f(x)        

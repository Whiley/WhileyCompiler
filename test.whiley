int f(int x) requires (x+1 > 0 && $ < 0), ensures x > 0:
    print str(x)
    return -1

int f(int x) requires (x > 0 && $ < 0),ensures x > 0:
    print str(x)
    return -1

int f(int x) ensures x > 0:
    print str(x)
    return -1

int f(int x) requires (x > 0 && $ < 0):
    print str(x)
    return -1


void System::main([string] args):
    f(|args|-1)

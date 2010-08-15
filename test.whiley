define pos as int where $ > 0
define neg as int where $ < 0

int f(pos x):
    return x

int f(neg x):
    return x

void System::main([string] args):
    int y = f(-11)
    print str(y)

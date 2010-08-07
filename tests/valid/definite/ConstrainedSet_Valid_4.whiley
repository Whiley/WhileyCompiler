define pintset as {int} requires |$| > 1

void System::main([string] args):
    pintset p
    p = {1,2}
    print str(p)

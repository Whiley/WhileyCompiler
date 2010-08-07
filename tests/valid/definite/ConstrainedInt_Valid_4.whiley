// this is a comment!
define nat as int requires $ < 10

nat f():
    return 1

void System::main([string] args):
    print str(f())

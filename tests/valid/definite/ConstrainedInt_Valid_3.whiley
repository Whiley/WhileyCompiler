// this is a comment!
define cr3nat as int requires $ < 10

cr3nat f(cr3nat x):
    return 1

void System::main([string] args):
    cr3nat y
    y = f(9)
    print str(y)

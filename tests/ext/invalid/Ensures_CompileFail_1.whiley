import whiley.lang.*:*

int f() ensures 2*$==1:
    return 1

void System::main([string] args):
    debug str(f())

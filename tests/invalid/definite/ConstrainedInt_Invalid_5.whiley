// this is a comment!
define {1,2,3,4,5} as nat

int h() ensures $ <= 3:
    return 0

nat f():
    return h()

void System::main([string] args):
    print str(f())

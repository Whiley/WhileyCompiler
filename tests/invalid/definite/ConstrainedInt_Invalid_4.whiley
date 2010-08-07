// this is a comment!
define c4nat as int requires $ < 10

int h() requires $ <= 5:
    return 5

c4nat f():
    return h() * 2

void System::main([string] args):
    print str(f())

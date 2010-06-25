// this is a comment!
define int where $ < 10 as c4nat

int h() ensures $ <= 5:
    return 5

c4nat f():
    return h() * 2

void System::main([string] args):
    print str(f())

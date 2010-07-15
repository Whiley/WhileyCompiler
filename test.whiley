define nat as int where $ >= 0

void f([nat] xs):
    print str(xs)

void System::main([string] args):
    f([-1,1])

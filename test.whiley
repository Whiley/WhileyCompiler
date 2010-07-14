define nat as int where $ >= 0

void f(nat x):
    print str(x)

void System::main([string] args):
    f(-1)

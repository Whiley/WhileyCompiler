define fr2nat as int requires $ >= 0

void f(fr2nat x):
    print str(x)

void System::main([string] args):
    fr2nat y
    y = 1
    f(y)

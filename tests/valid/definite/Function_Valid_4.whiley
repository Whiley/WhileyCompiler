define int where $ >= 0 as fr4nat

fr4nat g(fr4nat x):
    return x + 1

void f(fr4nat x):
    print str(x)

void System::main([string] args):
    fr4nat y
    y = 1
    f(g(y))

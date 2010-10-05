define fr4nat as int where $ >= 0

fr4nat g(fr4nat x):
    return x + 1

string f(fr4nat x):
    return str(x)

void System::main([string] args):
    y = 1
    out->println(f(g(y)))

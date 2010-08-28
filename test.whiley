define nat as int where $ >= 0

nat f(nat x):
    return x

void System::main([string] args):
    nat x = 1
    x = f(x)

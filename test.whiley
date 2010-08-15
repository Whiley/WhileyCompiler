int f(int y) requires y >= 0:
    return y+1

void System::main([string] args):
    f(|args|)

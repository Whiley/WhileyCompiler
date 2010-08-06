int f(int x) where x > 1:
    return x+1

int f(int x) where x < 1:
    return x+1

void System::main([[char]] args):
    int x = |args|
    print str(f(x))

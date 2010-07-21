[byte] f(int x) where x == 0 || x == 256:
    return [x]

void System::main([string] args):
    [byte] bytes = f(256)
    print str(bytes)


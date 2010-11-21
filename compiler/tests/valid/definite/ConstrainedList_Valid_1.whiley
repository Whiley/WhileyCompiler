[byte] f(int x) requires x == 0 || x == 169:
    return [x]

void System::main([string] args):
    bytes = f(0)
    out->println(str(bytes))


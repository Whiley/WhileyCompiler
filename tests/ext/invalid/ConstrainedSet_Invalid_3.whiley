{byte} f(int x) requires x == 0 || x == 256:
    return {x}

void System::main([string] args):
    bytes = f(256)
    debug str(bytes)


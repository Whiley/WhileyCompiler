int f(real x):
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

void System::main([string] args):
    out.println(str(f(1.23)))
    out.println(str(f(2.01)))
    out.println(str(f(3)))
    out.println(str(f(-1)))

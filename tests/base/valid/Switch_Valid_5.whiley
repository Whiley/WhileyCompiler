int f(int x):
    switch x:
        case 1:
        case 2:
            return -1
        case 3:
            return 1
    return 10

void System::main([string] args):
    out.println(str(f(1)))
    out.println(str(f(2)))
    out.println(str(f(3)))
    out.println(str(f(-1)))

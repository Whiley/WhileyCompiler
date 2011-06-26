int f(int x):    
    switch x:
        case 1:
            y = -1
            break
        case 2:
            y = -2
            break
        default:
            y = 0
    return y

void System::main([string] args):
    out.println(str(f(1)))
    out.println(str(f(2)))
    out.println(str(f(3)))
    out.println(str(f(-1)))

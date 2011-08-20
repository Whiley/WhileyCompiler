import whiley.lang.*:*

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
    this.out.println(str(f(1)))
    this.out.println(str(f(2)))
    this.out.println(str(f(3)))
    this.out.println(str(f(-1)))

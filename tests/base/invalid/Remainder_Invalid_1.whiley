int f(real x, int y):
    return x % y    

void System::main([string] args):
    out.println(str(f(10.23,5)))
    out.println(str(f(10.23,4)))
    out.println(str(f(1,4)))
    out.println(str(f(10.233,2)))
    out.println(str(f(-10.23,5)))
    out.println(str(f(-10.23,4)))
    out.println(str(f(-1,4)))
    out.println(str(f(-10.233,2)))
    out.println(str(f(-10.23,-5)))
    out.println(str(f(-10.23,-4)))
    out.println(str(f(-1,-4)))
    out.println(str(f(-10.233,-2)))
    out.println(str(f(10.23,-5)))
    out.println(str(f(10.23,-4)))
    out.println(str(f(1,-4)))
    out.println(str(f(10.233,-2)))

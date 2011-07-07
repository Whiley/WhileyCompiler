(int,int) f(real z):
    x,y = z
    return (x,y)

void System::main([string] args):
    out.println(str(f(10.0/5)))
    out.println(str(f(10.0/4)))
    out.println(str(f(1.0/4)))
    out.println(str(f(103.0/2)))
    out.println(str(f(-10.0/5)))
    out.println(str(f(-10.0/4)))
    out.println(str(f(-1.0/4)))
    out.println(str(f(-103.0/2)))
    out.println(str(f(-10.0/-5)))
    out.println(str(f(-10.0/-4)))
    out.println(str(f(-1.0/-4)))
    out.println(str(f(-103.0/-2)))
    out.println(str(f(10.0/-5)))
    out.println(str(f(10.0/-4)))
    out.println(str(f(1.0/-4)))
    out.println(str(f(103.0/-2)))

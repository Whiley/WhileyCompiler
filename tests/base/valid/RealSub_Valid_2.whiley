real suber(real x, real y, real z):
    return x - y - z

void System::main([string] args):
    out.println(str(suber(1.2,3.4,4.5)))
    out.println(str(suber(1000,300,400)))

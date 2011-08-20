import whiley.lang.*:*

real diver(real x, real y, real z):
    return x / y / z

void System::main([string] args):
    this.out.println(str(diver(1.2,3.4,4.5)))
    this.out.println(str(diver(1000,300,400)))

import whiley.lang.*:*

(int,int) f(real z):
    x,y = z
    return (x,y)

void System::main([string] args):
    this.out.println(str(f(10.0/5)))
    this.out.println(str(f(10.0/4)))
    this.out.println(str(f(1.0/4)))
    this.out.println(str(f(103.0/2)))
    this.out.println(str(f(-10.0/5)))
    this.out.println(str(f(-10.0/4)))
    this.out.println(str(f(-1.0/4)))
    this.out.println(str(f(-103.0/2)))
    this.out.println(str(f(-10.0/-5)))
    this.out.println(str(f(-10.0/-4)))
    this.out.println(str(f(-1.0/-4)))
    this.out.println(str(f(-103.0/-2)))
    this.out.println(str(f(10.0/-5)))
    this.out.println(str(f(10.0/-4)))
    this.out.println(str(f(1.0/-4)))
    this.out.println(str(f(103.0/-2)))

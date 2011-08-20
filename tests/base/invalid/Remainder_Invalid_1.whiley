import whiley.lang.*:*

int f(real x, int y):
    return x % y    

void System::main([string] args):
    this.out.println(str(f(10.23,5)))
    this.out.println(str(f(10.23,4)))
    this.out.println(str(f(1,4)))
    this.out.println(str(f(10.233,2)))
    this.out.println(str(f(-10.23,5)))
    this.out.println(str(f(-10.23,4)))
    this.out.println(str(f(-1,4)))
    this.out.println(str(f(-10.233,2)))
    this.out.println(str(f(-10.23,-5)))
    this.out.println(str(f(-10.23,-4)))
    this.out.println(str(f(-1,-4)))
    this.out.println(str(f(-10.233,-2)))
    this.out.println(str(f(10.23,-5)))
    this.out.println(str(f(10.23,-4)))
    this.out.println(str(f(1,-4)))
    this.out.println(str(f(10.233,-2)))

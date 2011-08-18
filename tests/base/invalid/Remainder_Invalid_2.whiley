int f(int x, real y):
    return x % y    

void System::main([string] args):
    this.out.println(str(f(10,5.23)))
    this.out.println(str(f(10,4)))
    this.out.println(str(f(1,4)))
    this.out.println(str(f(103,2)))
    this.out.println(str(f(-10,5.23)))
    this.out.println(str(f(-10,4)))
    this.out.println(str(f(-1,4)))
    this.out.println(str(f(-103,2)))
    this.out.println(str(f(-10,-5.23)))
    this.out.println(str(f(-10,-4)))
    this.out.println(str(f(-1,-4)))
    this.out.println(str(f(-103,-2)))
    this.out.println(str(f(10,-5.23)))
    this.out.println(str(f(10,-4)))
    this.out.println(str(f(1,-4)))
    this.out.println(str(f(103,-2)))

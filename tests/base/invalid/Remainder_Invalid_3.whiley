int f(real x, real y):
    return x % y    

void System::main([string] args):
    this.out.println(str(f(10.5343,5.2354)))
    this.out.println(str(f(10.5343,4.2345)))
    this.out.println(str(f(1,4.2345)))
    this.out.println(str(f(10.53433,2)))
    this.out.println(str(f(-10.5343,5.2354)))
    this.out.println(str(f(-10.5343,4.2345)))
    this.out.println(str(f(-1,4.2345)))
    this.out.println(str(f(-10.53433,2)))
    this.out.println(str(f(-10.5343,-5)))
    this.out.println(str(f(-10.5343,-4)))
    this.out.println(str(f(-1,-4)))
    this.out.println(str(f(-10.53433,-2)))
    this.out.println(str(f(10.5343,-5)))
    this.out.println(str(f(10.5343,-4)))
    this.out.println(str(f(1,-4)))
    this.out.println(str(f(10.53433,-2)))

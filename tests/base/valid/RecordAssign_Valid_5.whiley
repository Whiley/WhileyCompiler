import whiley.lang.*:*

define bytes as { int b1, int b2 }

bytes f(int a):
    bs = {b1:a,b2:a+1}
    return bs

void System::main([string] args):
    this.out.println(str(f(1)))
    this.out.println(str(f(2)))
    this.out.println(str(f(9)))

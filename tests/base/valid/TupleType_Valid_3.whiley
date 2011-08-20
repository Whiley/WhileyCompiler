import whiley.lang.*:*

(int,int) f(int x):
    return (x,x+2)

void System::main([string] args):
    x,y = f(1)
    this.out.println(str(x))
    this.out.println(str(y))

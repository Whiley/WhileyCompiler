import whiley.lang.*:*

int f(int x, int y):
    return x / y

void System::main([string] args):
     x = f(10,2)
     this.out.println(str(x)  )

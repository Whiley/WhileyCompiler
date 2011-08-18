int f(int x, int y) requires y != 0:
    return x / y

void System::main([string] args):
     x = f(10,2)
     this.out.println(str(x)  )

void System::f([int] x):
    z = |x|
    this.out.println(str(z))
    this.out.println(str(x[z-1]))

void System::main([string] args):
     arr = [1,2,3]
     // following line should block
     this.f(arr)

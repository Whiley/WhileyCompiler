void System::f([int] x):
    z = |x|
    out<->println(str(z))
    out<->println(str(x[z-1]))

void System::main([string] args):
     arr = [1,2,3]
     // following line should block
     this<->f(arr)

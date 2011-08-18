define Proc as process { real(real) func }

int Proc::func(int x):
    return x + 1

real Proc::test(real arg):
    return this.func(arg)

real id(real x):
    return x
    
void System::main([string] args):
    p = spawn { func: &id }
    x = p.test(123)
    this.out.println("GOT: " + str(x))
    x = p.test(12545)
    this.out.println("GOT: " + str(x))
    x = p.test(-11)
    this.out.println("GOT: " + str(x))

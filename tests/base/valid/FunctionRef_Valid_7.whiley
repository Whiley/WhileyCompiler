import whiley.lang.*:*

define Proc as process { int data }

int Proc::read(int x):
    return x + 1

int System::test(Proc p, int arg):
    return p.read(arg)
    
void System::main([string] args):
    p = spawn {data: 1}
    x = this.test(p,123)
    this.out.println("GOT: " + str(x))
    x = this.test(p,12545)
    this.out.println("GOT: " + str(x))
    x = this.test(p,-11)
    this.out.println("GOT: " + str(x))

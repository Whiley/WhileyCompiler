import * from whiley.lang.*

define Proc as process { int(int) func }

int Proc::func(int x):
    return x + 1

int Proc::test(int arg):
    return this.func(arg)

int id(int x):
    return x
    
void ::main(System sys,[string] args):
    p = spawn { func: &id }
    x = p.test(123)
    sys.out.println("GOT: " + str(x))
    x = p.test(12545)
    sys.out.println("GOT: " + str(x))
    x = p.test(-11)
    sys.out.println("GOT: " + str(x))

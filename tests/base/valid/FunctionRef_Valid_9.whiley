import * from whiley.lang.*

define Proc as process { int(int) func }

int Proc::func(int x):
    return x + 1

int Proc::test(int arg):
    return this->func(arg)

int id(int x):
    return x
    
void ::main(System.Console sys):
    p = spawn { func: &id }
    x = p.test(123)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(-11)
    sys.out.println("GOT: " + Any.toString(x))

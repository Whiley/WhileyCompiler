import println from whiley.lang.System

define Proc as ref { int(int) func }

int Proc::func(int x):
    return x + 1

int Proc::test(int arg):
    return this->func(arg)

int id(int x):
    return x
    
void ::main(System.Console sys):
    p = new { func: &id }
    x = p.test(123)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = p.test(-11)
    sys.out.println("GOT: " + Any.toString(x))

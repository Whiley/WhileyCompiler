

define Proc as process { int data }

int Proc::read(int x):
    return x + 1

int ::test(Proc p, int arg):
    return p.read(arg)
    
void ::main(System.Console sys):
    p = spawn {data: 1}
    x = test(p,123)
    sys.out.println("GOT: " + Any.toString(x))
    x = test(p,12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = test(p,-11)
    sys.out.println("GOT: " + Any.toString(x))

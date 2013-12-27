import println from whiley.lang.System

define Func as {
    int(int) read
}

int id(int x):
    return x

int test(Func f, int arg):
    return f.read(arg)
    
void ::main(System.Console sys):
    x = test({read: &id},123)
    sys.out.println("GOT: " + Any.toString(x))
    x = test({read: &id},12545)
    sys.out.println("GOT: " + Any.toString(x))
    x = test({read: &id},-11)
    sys.out.println("GOT: " + Any.toString(x))

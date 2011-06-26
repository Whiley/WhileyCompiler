define Proc as process { int data }

int Proc::read(int x):
    return x + 1

define Func as {
    int(int) read
}

int id(int x):
    return x

int test(Func f, int arg):
    return f.read(arg)
    
void System::main([string] args):
    x = test({read: &id},123)
    out.println("GOT: " + str(x))
    x = test({read: &id},12545)
    out.println("GOT: " + str(x))
    x = test({read: &id},-11)
    out.println("GOT: " + str(x))

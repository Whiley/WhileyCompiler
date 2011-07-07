define Func as {
    int read(int)
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

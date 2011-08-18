real read(real a):
    return -a

int id(int x):
    return x

real test(int(int) read, real arg):    
    return read(arg)
    
void System::main([string] args):
    x = test(&id,1)
    this.out.println(str(x))
    x = test(&id,123)
    this.out.println(str(x))
    x = test(&id,223)
    this.out.println(str(x))
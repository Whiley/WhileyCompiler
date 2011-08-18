int f([string] r):
    return |r|
 
void System::main([string] args):
    r = args + [1]
    f(r)
    this.out.println(str(r))

define bytes as { int b1, int b2 }

bytes f(int a):
    bs = {b1:a,b2:a+1}
    return bs

void System::main([string] args):
    out.println(str(f(1)))
    out.println(str(f(2)))
    out.println(str(f(9)))

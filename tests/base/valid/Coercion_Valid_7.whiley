int f(int|bool x):
    if x ~= int:
        return x
    else:
        return 1 

void System::main([string] args):
    out.println(str(f(true)))
    out.println(str(f(1)))

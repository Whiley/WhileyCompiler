int f(int x):
    if(x < 10):
        return 1
    else:
        return 2

void System::main([string] args):
    out->println(str(f(1)))
    out->println(str(f(10)))
    out->println(str(f(11)))
    out->println(str(f(1212)))
    out->println(str(f(-1212)))

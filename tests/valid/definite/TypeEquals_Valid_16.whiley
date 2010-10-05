void f(int|null x):
    if x ~= null:
        out->println("GOT NULL")
    else:
        out->println("GOT INT")

void System::main([string] args):
    x = null
    f(x)
    f(1)

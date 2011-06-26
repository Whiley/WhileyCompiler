string f(bool b):
    return str(b)

void System::main([string] args):
    x = true
    out.println(f(x))
    x = false
    out.println(f(x))

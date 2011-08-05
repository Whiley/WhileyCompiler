string g(int z) requires z > 1:
    return str(z)

string f(int x) requires x > 0:
    y = x + 1
    return g(y)

void System::main([string] args):
    out.println(f(1))

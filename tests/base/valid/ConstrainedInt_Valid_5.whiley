// this is a comment!
define num as {1,2,3,4}

string f(num x):
    y = x
    return str(y)

string g(int x, int z):
    return f(z)

void System::main([string] args):
    out.println(g(1,2))

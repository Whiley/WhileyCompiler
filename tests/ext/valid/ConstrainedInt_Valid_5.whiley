// this is a comment!
define num as {1,2,3,4}

string f(num x):
    y = x
    return str(y)

string g(int x, int z) requires (x == 1 || x == 2) && z in {1,2,3,x}:
    return f(z)

void System::main([string] args):
    this.out.println(g(1,2))

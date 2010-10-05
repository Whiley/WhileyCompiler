define fcode as {1,2,3,4}
define tcode as {1,2}

string g(fcode f):
    return str(f)

void System::main([string] args):
    x = 1
    out->println(g(x))

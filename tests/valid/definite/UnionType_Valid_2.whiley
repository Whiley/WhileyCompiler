// this is a comment!
define IntReal as int|real

string f(IntReal y):
    return str(y)

void System::main([string] args):
    x = 123
    out->println(f(x))
    x = 1.234
    out->println(f(x))


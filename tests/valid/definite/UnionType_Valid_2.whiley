// this is a comment!
define IntReal as int|real

void f(IntReal y):
    print str(y)

void System::main([string] args):
    x = 123
    f(x)
    x = 1.234
    f(x)


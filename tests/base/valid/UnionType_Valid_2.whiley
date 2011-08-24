import whiley.lang.*:*

// this is a comment!
define IntReal as int|real

string f(IntReal y):
    return str(y)

void ::main(System sys,[string] args):
    x = 123
    sys.out.println(f(x))
    x = 1.234
    sys.out.println(f(x))


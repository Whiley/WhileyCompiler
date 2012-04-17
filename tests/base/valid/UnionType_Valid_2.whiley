import println from whiley.lang.System

// this is a comment!
define IntReal as int|real

string f(IntReal y):
    return Any.toString(y)

void ::main(System.Console sys):
    x = 123
    sys.out.println(f(x))
    x = 1.234
    sys.out.println(f(x))


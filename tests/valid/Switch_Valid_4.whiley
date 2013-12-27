import println from whiley.lang.System

define Red as 1
define Blue as 2
define Green as 3

define RGB as {Red,Blue,Green}

int f(RGB c):
    switch c:
        case Red:
            return 123
        case Switch_Valid_8.Blue:
            return 234
        default:
            return 456

void ::main(System.Console sys):    
    sys.out.println("NUM: " + f(Red))
    sys.out.println("NUM: " + f(Green))
    sys.out.println("NUM: " + f(Blue))

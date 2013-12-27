import println from whiley.lang.System

void ::main(System.Console sys):
    x = true
    y = false
    sys.out.println(x)
    sys.out.println(y)
    sys.out.println("AND")
    x = x && y
    sys.out.println(x)
    sys.out.println("NOT")
    sys.out.println(!x)

import * from whiley.lang.*

void ::main(System sys,[string] args):
    x = true
    y = false
    sys.out.println(str(x))
    sys.out.println(str(y))
    sys.out.println("AND")
    x = x && y
    sys.out.println(str(x))
    sys.out.println("NOT")
    sys.out.println(str(!x))

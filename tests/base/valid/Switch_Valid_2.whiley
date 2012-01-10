import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    x = 1
    switch x:
        case 1:
            sys.out.println("CASE 1")
            return
        case 2:
            sys.out.println("CASE 2")
            return
    sys.out.println("DEFAULT CASE")

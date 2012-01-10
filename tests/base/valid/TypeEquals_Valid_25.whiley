import * from whiley.lang.*

define src as int|[src]

string f(src e):
    if e is [any]:
        return "[*]"
    else:
        return "int"

void ::main(System.Console sys):
    sys.out.println(f([1]))
    sys.out.println(f([[1]]))
    sys.out.println(f([[[1]]]))
    sys.out.println(f(1))

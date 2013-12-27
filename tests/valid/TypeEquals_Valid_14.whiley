import println from whiley.lang.System

define src as int|[int]|[[int]]

string f(src e):
    if e is [any]:
        return "[*]"
    else:
        return "int"

void ::main(System.Console sys):
    sys.out.println(f([1,2,3]))
    sys.out.println(f([[1],[2]]))
    sys.out.println(f(1))

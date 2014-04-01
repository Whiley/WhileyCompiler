import whiley.lang.System

type liststr is [int] | string

function index(liststr l, int index) => any:
    return l[index]

method main(System.Console sys) => void:
    [int] l = [1, 2, 3]
    sys.out.println(index(l, 1))
    sys.out.println(index(l, 2))
    string s = "Hello World"
    sys.out.println(index(s, 0))
    sys.out.println(index(s, 2))

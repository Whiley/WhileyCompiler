import whiley.lang.*

type liststr is [int] | [int]

function index(liststr l, int index) -> any
    requires index >= 0 && index < |l|:
    //
    return l[index]

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    sys.out.println(index(l, 1))
    sys.out.println(index(l, 2))
    [int] s = "Hello World"
    sys.out.println(index(s, 0))
    sys.out.println(index(s, 2))

import whiley.lang.System

type listdict is [int] | {int=>string}

function index(listdict l, int index) => any:
    return l[index]

method main(System.Console sys) => void:
    l = [1, 2, 3]
    sys.out.println(index(l, 1))
    sys.out.println(index(l, 2))
    l = {1=>"hello", 2=>"cruel", 3=>"world"}
    sys.out.println(index(l, 2))
    sys.out.println(index(l, 3))

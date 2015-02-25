import whiley.lang.System

type fcode is (int x) where x in {1, 2, 3, 4}

function g(fcode f) -> string:
    return Any.toString(f)

method main(System.Console sys) -> void:
    int x = 1
    sys.out.println(g(x))

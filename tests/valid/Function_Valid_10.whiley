import whiley.lang.*

type fcode is (int x) where x in {1, 2, 3, 4}

function g(fcode f) -> int:
    return f

method main(System.Console sys) -> void:
    int x = 1
    sys.out.println(g(x))

import whiley.lang.*

type cr2num is (int x) where x in {1, 2, 3, 4}

function f(cr2num x) -> int:
    int y = x
    return y

method main(System.Console sys) -> void:
    sys.out.println(f(3))

import whiley.lang.*

function f(int x) -> (int r)
requires x != 0
ensures r != 1:
    //
    return x + 1

method main(System.Console sys) -> void:
    sys.out.println(f(9))

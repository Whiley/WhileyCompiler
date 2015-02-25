
type odd is (int x) where x in {1, 3, 5}

function f(odd x) -> int:
    return x

method main(System.Console sys) -> void:
    int y = 2
    f(y)
    debug Any.toString(y)

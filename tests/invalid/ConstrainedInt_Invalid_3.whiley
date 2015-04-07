
type odd is (int x) where x in {1, 3, 5}

function f(odd x) -> int:
    return x

method main() -> int:
    int y = 2
    return f(y)

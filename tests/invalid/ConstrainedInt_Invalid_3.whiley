
type odd is (int x) where x == 1 || x == 3 || x == 5

function f(odd x) -> int:
    return x

method main() -> int:
    int y = 2
    return f(y)

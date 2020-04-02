
type odd is (int x) where x == 1 || x == 3 || x == 5

function f(odd x) -> int:
    return x

public export method test():
    int y = 2
    assume f((odd) y) == 2

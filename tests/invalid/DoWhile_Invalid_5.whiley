method g(int x) -> int:
    int i = 0
    do:
        i = i + 1
    while i < x where i >= g(x)
    return x

function f(int x, int y) -> int
requires (x >= 0) && (y > 0):
    int z
    if x < y:
        z = 1
    return z

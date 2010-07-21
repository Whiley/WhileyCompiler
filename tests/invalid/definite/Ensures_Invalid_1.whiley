int g(int y) where y >= 0 && $ > 0:
    return y

int f(int y) where y > 0 && $ >= 0:
    return g(y)

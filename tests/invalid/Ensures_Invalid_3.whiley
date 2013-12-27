

int g(int y) requires y >= 0, ensures $ > 0:
    return y

int f(int y) requires y > 0, ensures $ >= 0:
    return g(y)

int g(int y) where y > 0:
    return 10 / y

void f(int y) where y >= 0:
    g(y)

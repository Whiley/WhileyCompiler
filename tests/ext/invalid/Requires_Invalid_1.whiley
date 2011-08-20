import whiley.lang.*:*

int g(int y) requires y > 0:
    return 10 / y

void f(int y) requires y >= 0:
    g(y)

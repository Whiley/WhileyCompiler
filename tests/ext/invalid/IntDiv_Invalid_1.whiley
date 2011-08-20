import whiley.lang.*:*

void f(int x, int y) requires y >= 0:
     x = x / y

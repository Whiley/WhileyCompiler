property nat_a(int x) -> (bool r):
    x >= 0

property nat_b(int x) -> (bool r):
    x >= 0

function id(int x) -> (int y)
requires nat_a(x)
ensures nat_b(y):
    return x


public export method test():
    assume id(0) == 0
    assume id(1) == 1
    assume id(2) == 2


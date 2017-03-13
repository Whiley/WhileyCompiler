property nat_a(int x) where x >= 0
property nat_b(int x) where x > 0

function id(int x) -> (int y)
requires nat_a(x)
ensures nat_b(x):
    return x


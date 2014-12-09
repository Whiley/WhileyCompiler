function greater(int x,int y) -> (int z)
requires x != y
ensures z == x || z == y:
    if (x > y):
        return x
    else if (y > x):
        return y


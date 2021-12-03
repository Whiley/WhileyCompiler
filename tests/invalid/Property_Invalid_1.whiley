property nat(int x) -> (bool r):
    x > 0

function abs(int x) -> (int y)
ensures nat(y)
ensures (x == y) || (x == -y):
    //
    if x >= 0:
        return x
    else:
        return y
    

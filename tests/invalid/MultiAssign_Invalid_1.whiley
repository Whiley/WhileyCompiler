type nat is (int x) where x >= 0

function f(int x, int y) -> (int xp, int yp):
    return y,x

function min(nat a, int b) -> int:
    a,b = f(a,b)
    if x > y:
        return y
    else:
        return x

method main() :
    int x = min(0,-1)

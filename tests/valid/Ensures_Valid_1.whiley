

function add(int x, int y) -> (int r)
requires x >= 0 && y >= 0
ensures r > 0:
    //
    if x == y:
        return 1
    else:
        return x + y

public export method test() :
    assume add(1,2) == 3
    assume add(1,1) == 1

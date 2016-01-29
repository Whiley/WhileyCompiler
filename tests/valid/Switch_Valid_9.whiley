

function f(int x) -> (int r)
// Return cannot be 1
ensures r != 1:
    //
    switch x:
        case 1:
            return 2
        case 2:
            return 2
    return x

public export method test() :
    assume f(2) == 2
    assume f(1) == 2
    assume f(0) == 0

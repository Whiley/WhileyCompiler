

function f(int x) -> (int r)
// Return is between 0 and 2
ensures r >= 0 && r <= 2:
    //
    switch x:
        case 1:
            return 0
        case 2:
            return 1
    return 2

public export method test() :
    assume f(2) == 1
    assume f(1) == 0
    assume f(0) == 2

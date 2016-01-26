

type nat is (int n) where n >= 0

function f(int x) -> (int r)
// Input cannot be negative
requires x >= 0
// Return is either 0 or 1
ensures r == 0 || r == 1:
    //
    switch x:
        case 1:
            return 1
        default:
            return 0

public export method test() :
    assume f(2) == 0
    assume f(1) == 1
    assume f(0) == 0

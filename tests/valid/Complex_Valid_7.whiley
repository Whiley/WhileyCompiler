

function max3(int x, int y, int z) -> (int r)
// Return value must be as large as each parameter
ensures r >= x && r >= y && r >= z
// Return value must match at least one parameter
ensures r == x || r == y || r == z:
    //
    bool isX = x >= y && x >= z
    bool isY = y >= x && y >= z
    //
    if isX:
        return x
    else if isY:
        return y
    else:
        return z

// Following is just to help verification
method fn([int] xs):
    int i1 = 0
    while i1 < |xs| where i1 >= 0:
        int v1 = xs[i1]
        int i2 = 0
        while i2 < |xs| where i2 >= 0:
            int v2 = xs[i2]
            int i3 = 0
            while i3 < |xs| where i3 >= 0:
                int v3 = xs[i3]
                assume (v1 <= v3 && v2 <= v3) ==> max3(v1,v2,v3) == v3
                assume (v1 <= v2 && v3 <= v2) ==> max3(v1,v2,v3) == v2
                assume (v2 <= v3 && v3 <= v1) ==> max3(v1,v2,v3) == v1
                i3 = i3 + 1
            //
            i2 = i2 + 1
        //
        i1 = i1 + 1
    // Done.

public export method test():
    fn([1,2,3,4,5,6,7,8])

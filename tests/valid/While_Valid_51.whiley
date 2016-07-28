function f(int[] xs) -> (bool r)
ensures r ==> all { j in 0..|xs| | xs[j] != 0}:
    //
    int i = 0
    //
    while i < |xs|
    where i >= 0
    where all { j in 0..i | xs[j] != 0}:
        //
        if xs[i] == 0:
            return false
        i = i + 1
    //
    return true

public export method test():
    assume f([1,2,3,4])
    assume !f([0,2,4,3])
    assume f([1])

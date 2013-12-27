import println from whiley.lang.System

// non-deterministically select an element from a set
int f([int] xs) 
    requires |xs| > 0, 
    ensures some { 
        i in 0..|xs| | $ == xs[i] 
    }:
    //
    r = xs[0]
    i = 1
    while i < |xs| where i >= 1 && some { j in 0..i | r == xs[j] }:
        r = xs[i]
        i = i + 1
    //
    return r

void ::main(System.Console sys):
    sys.out.println(f([1,2,3,4,5,6,7,8,9,10]))


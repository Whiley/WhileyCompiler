import println from whiley.lang.System

// This is a pretty tough verification problem!!

// Find the maximum integer from a list
int max([int] xs) 
    requires |xs| > 0, 
    ensures $ in xs && no { x in xs | x > $ }:
    //
    r = xs[0]
    i = 0
    while i < |xs| where r in xs && no { j in 0..i | xs[j] > r }:
        r = Math.max(r,xs[i])
        i = i + 1
    return r

void ::main(System.Console sys):
    sys.out.println(max([1,2,3,4,5,6,7,8,9,10]))
    sys.out.println(max([-8,7,9,1,-1,2,5,6,-200,4]))    
    sys.out.println(max([1]))    


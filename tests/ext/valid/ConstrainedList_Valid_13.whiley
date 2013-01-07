import println from whiley.lang.System

define nat as int where $ >= 0

[nat] inc([nat] xs):
    i = 0
    //
    // missing appropriate loop invariant.
    //
    for j in xs where i >= 0:
        if i < |xs|:
            xs[i] = xs[i] + 1
        i = i + 1
    //
    assume no {x in xs | x < 0}
    return xs


void ::main(System.Console sys):
    sys.out.println(Any.toString(inc([0])))
    sys.out.println(Any.toString(inc([1,2,3])))
    sys.out.println(Any.toString(inc([10,9,8,7,6,5,4,3,2,1,0])))

    

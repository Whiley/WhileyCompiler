import println from whiley.lang.System

define nat as int where $ >= 0

[nat] inc([nat] src) ensures |$| == |src| && no { x in 0..|src| | $[x] <= 0 }:
    i = 0
    while i < |src| where i >= 0 && no { x in 0..i | src[x] <= 0 }:
        src[i] = src[i] + 1
        i = i + 1
    return src

void ::main(System.Console sys):
    xs = [1,3,5,7,9,11]
    xs = inc(xs)
    sys.out.println(Any.toString(xs))



    
    
    
import println from whiley.lang.System

define nat as int where $ >= 0

nat f([nat] xs, [nat] ys, nat i) requires i < |xs| + |ys|:
    xs = xs + ys
    return xs[i]

void ::main(System.Console sys):
    left = [1,2,3]
    right = [5,6,7]
    r = f(left,right,1)
    sys.out.println(Any.toString(r))
    r = f(left,right,4)
    sys.out.println(Any.toString(r))

    

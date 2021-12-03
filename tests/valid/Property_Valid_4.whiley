property contains(int[] xs, int x) -> (bool r):
    return some { i in 0..|xs| | xs[i] == x }

function id(int[] xs, int x) -> (int[] ys)
requires contains(xs,x)
ensures contains(ys,x):
    return xs

public export method test():
    assume id([0],0) == [0]
    assume id([1,2],1) == [1,2]
    assume id([1,2],2) == [1,2]    
    assume id([1,2,3],1) == [1,2,3]
    assume id([1,2,3],2) == [1,2,3]
    assume id([1,2,3],3) == [1,2,3]


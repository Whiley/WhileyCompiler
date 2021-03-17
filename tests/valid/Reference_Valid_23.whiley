type Ref<T> is &T

method fill(Ref<int>[] refs, int n)
ensures all { k in 0..|refs| | *(refs[k]) == n }:
    //
    for i in 0..|refs|:
        *(refs[i]) = n

method to_array(Ref<int>[] refs) -> (int[] vals)
ensures |vals| == |refs|
ensures all { k in 0..|refs| | *(refs[k]) == vals[k] }:
    //
    int[] vs = [0;|refs|]
    //
    for i in 0..|vs|
    where |vs| == |refs|
    where all { k in 0..i | *(refs[k]) == vs[k] }:
        vs[i] = *(refs[i])
    //
    return vs
        

public export method test():
    Ref<int>[] rs = [new 1, new 2, new 3]
    int[] xs = to_array(rs)
    //
    assert xs == [1,2,3]
    //
    fill(rs,0)
    //
    xs = to_array(rs)
    //
    assert xs == [0,0,0]

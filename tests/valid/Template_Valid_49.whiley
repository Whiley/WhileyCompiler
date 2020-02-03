type Box<T> is { T val }
type Option<T> is null | Box<T>

function empty<T>() -> Option<T>:
    return null

function id<T>(T x) -> (T r):
    return x

public export method test():
    Option<int> i = empty()
    Option<bool> j = id(empty())
    Option<int[]> k = id(id(empty()))
    // Try empty cases
    assume i == null
    assume j == null
    assume k == null
    // Try full cases
    i = {val: 1}
    j = {val: false}
    k = {val: [1,2,3]}
    //
    assume i.val == 1
    assume j.val == false
    assume k.val == [1,2,3]
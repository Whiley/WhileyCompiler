type Box<T> is { T val }
type Option<T> is null | Box<T>

function empty<T>() -> (Option<T> a, Option<T> b):
    return null,null

public export method test():
    (Option<int> i, Option<int> j) = empty()
    //
    assume i == null
    assume j == null

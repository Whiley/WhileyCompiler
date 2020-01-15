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
    //
    assume i == null
    assume j == null
    assume k == null
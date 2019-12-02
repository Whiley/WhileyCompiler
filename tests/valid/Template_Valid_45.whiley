type Box<T> is { T contents }
type Optional<T> is null | Box<T>

function empty_boxes<T>(T t) -> (T xs, Optional<T>[] ys)
ensures ys == []:
    //
    return t,[]

public export method test():
    Optional<int>[] bs
    int t
    byte b = 0b001
    (t,bs) = empty_boxes<int>(0)
    assert bs == [{contents:0};0]

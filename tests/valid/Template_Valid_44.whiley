type Box<T> is { T contents }

function empty_boxes<T>(T t) -> (T xs, Box<T>[] ys):
    return t,[]

public export method test():
    Box<int>[] bs
    int t
    (t,bs) = empty_boxes<int>(0)
    assert bs == [{contents:0};0]

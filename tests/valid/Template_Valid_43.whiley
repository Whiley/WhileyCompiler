type Box<T> is { T contents }

function empty_boxes<T>() -> (Box<T>[] r)
ensures r == []:
    return []

public export method test():
    assert empty_boxes<int>() == [{contents:0};0]
    assert empty_boxes<bool>() == [{contents:false};0]

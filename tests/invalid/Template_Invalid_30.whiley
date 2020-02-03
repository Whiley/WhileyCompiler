type Box<T> is { &T ptr }

function bigger(Box<int> b) -> (Box<int|bool> r):
    return b

function smaller(Box<int|bool> b) -> (Box<int> r):
    return b
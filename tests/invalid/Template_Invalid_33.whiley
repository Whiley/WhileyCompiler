type Box<T> is { &T ptr }
type Outer<T> is Box<T>

function bigger(Outer<int> b) -> (Outer<int|bool> r):
    return b

function smaller(Outer<int|bool> b) -> (Outer<int> r):
    return b
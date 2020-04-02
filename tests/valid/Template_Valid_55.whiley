type Box<T> is { T val }
type Option<T> is null | Box<T>

function empty<T>() -> Option<T>:
    return null

function id<T>(T x) -> {T f}:
    return { f:x }

public export method test():
    { {int val}|null f } i = id(empty())
    //
    assume i.f == null

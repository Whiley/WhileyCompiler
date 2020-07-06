function id<T>(T x) -> (T y):
    return x

function f(int x) -> (int y):
    return id<x>(x)

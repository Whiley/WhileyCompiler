method f<T>(T x) -> (bool y):
    return false

function f(int x) -> (int y)
requires f(x):
    //
    return x
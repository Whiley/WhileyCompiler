method f(&int x, &any y) -> (int r):
    *y = true
    return *x

method g() -> (int r):
    &int p = new 1
    return f(p,p)

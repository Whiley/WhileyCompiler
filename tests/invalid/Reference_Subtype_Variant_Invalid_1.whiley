method f(&int x, &(int|bool) y) -> (int r):
    *y = true
    return *x

method g() -> (int r):
    &int p = new 1
    return f(p,p)

method m(&int x)
ensures *x > old(*x):
    // increment
    *x = *x + 1

public export method test():
    &int l = new 0
    &int k = new 1
    m(k)
    assert (*l) == 0
    assert (*k) > 1

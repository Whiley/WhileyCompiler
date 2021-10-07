method m(&int x)
ensures *x == 42:
    *x = 42

public export method test():
    &int x = new 1
    assert (*x) == 1
    m(x)
    assert (*x) == 42

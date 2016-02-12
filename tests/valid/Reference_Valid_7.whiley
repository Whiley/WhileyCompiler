method m(&int x):
    *x = 42

public export method test():
    &int x = new 1
    assume (*x) == 1
    m(x)
    assume (*x) == 42

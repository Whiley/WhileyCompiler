method m(&int p)
ensures *p == old(*p):
    skip

public export method test():
    &int p = new 1
    m(p)
    assert *p == 1
    
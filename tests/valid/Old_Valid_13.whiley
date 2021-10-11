variant unchanged(&int p)
where *p == old(*p)

method m(&int p)
ensures unchanged(p):
    skip

public export method test():
    &int p = new 1
    m(p)
    assert *p == 1
    
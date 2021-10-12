method m(&(&int) p)
ensures *p == old(*p):
ensures **p == old(**p):
    skip

public export method test():
    &int p = new 1
    &(&int) q = new p
    m(q)
    assert *p == 1
    
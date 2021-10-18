variant unchanged(&{int f} p)
where p->f == old(p->f)

method m(&{int f} p)
ensures unchanged(p):
    skip

public export method test():
    &{int f} p = new {f:1}
    m(p)
    assert p->f == 1
    
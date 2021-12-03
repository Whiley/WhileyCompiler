property broken(&int p) -> (bool r):
    *p == old(*p)

method m(&int x):
    //
    assert broken(x)
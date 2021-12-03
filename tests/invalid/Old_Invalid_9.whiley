property broken(&int p) -> (bool r):
    return *p == old(*p)

method m(&int x):
    //
    assert broken(x)
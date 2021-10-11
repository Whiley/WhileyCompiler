variant broken(&int p)
where *p == old(*p)

method m(&int x)
requires broken(x):
    //
    skip
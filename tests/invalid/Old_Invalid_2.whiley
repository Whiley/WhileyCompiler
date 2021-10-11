method m(&int p)
// old doesn't make sense in this context
ensures *p == old(p):
    //
    skip
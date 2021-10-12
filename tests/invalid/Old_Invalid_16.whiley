method m(&(&int) p)
ensures **p == old(**p):
    *p = new **p

public export method test():
    &int p = new 1
    &(&int) q = new p
    m(q)   
    // The following assertion cannot be shown because the
    // postcondition for m() is not strong enough.     
    assert *p == 1
    
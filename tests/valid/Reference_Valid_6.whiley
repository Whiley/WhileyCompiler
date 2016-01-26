method m(&(&int) x):
    // x = -> [ -> [ 0 ] ]
    &int y = *x
    // y = -> [ 0 ]
    *(*x) = 1
    // x = -> [ -> [ 1 ] ]
    // y = -> [ 1 ]
    assert (*y) == 1

public export method test():
    &int l = new 0    // l = -> [ 0 ]
    &(&int) k = new l // k = -> [ -> [ 0 ] ] 
    m(k)
    assert (*l) == 1
    assert (*(*k)) == 1

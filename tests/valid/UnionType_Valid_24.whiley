public export method test():
    &(bool|int) x = new (bool|int) 1
    *x = false
    //
    assert *x == false

public export method main():
    int i = 0
    &int q = new 1
    &int p = new 2
    //
    do:
        int x = *p
        *q = 1
        i = i + 1
    while i < 10
    //
    assert *p == 2

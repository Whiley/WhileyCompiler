method m(&(int[]) p)
ensures |*p| == |old(*p)|:
    //
    int len = |*p|
    //
    for i in 0..len:
        (*p)[i] = 0

public export method test():
    &(int[]) p = new [1,2,3]
    m(p)
    assert |*p| == 3

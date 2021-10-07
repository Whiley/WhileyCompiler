method read(int|&int p) -> (int r)
ensures (p is int) ==> (r == p)
ensures (p is &int) ==> (r == *p)
ensures (p is &int) ==> (*p == old(*p)):
    //
    if p is &int:
        return *p
    else:
        return p

public export method test():
    int i = 123
    &int ptr = new 1
    // Check integers
    i = read(i)
    assert i == 123
    // Check references
    i = read(ptr)
    assert i == 1
    *ptr = 2
    i = read(ptr)    
    assert i == 2
    // Check ptr unchanged
    assert *ptr == 2
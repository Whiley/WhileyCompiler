method read(int|&int p) -> (int r):
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
    assume i == 123
    // Check references
    i = read(ptr)
    assume i == 1
    *ptr = 2
    i = read(ptr)    
    assume i == 2


method swap(&int x_ptr, &int y_ptr)
ensures *x_ptr == old(*y_ptr)
ensures *y_ptr == old(*x_ptr):
    int tmp = *x_ptr
    *x_ptr = *y_ptr
    *y_ptr = tmp

public export method test():
    &int x = new 1
    &int y = new 2
    assert (*x) == 1
    assert (*y) == 2
    swap(x,y)
    assert (*x) == 2
    assert (*y) == 1



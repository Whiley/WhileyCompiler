

method swap(&int x_ptr, &int y_ptr):
    int tmp = *x_ptr
    *x_ptr = *y_ptr
    *y_ptr = tmp

public export method test():
    &int x = new 1
    &int y = new 2
    assume (*x) == 1
    assume (*y) == 2
    swap(x,y)
    assume (*x) == 2
    assume (*y) == 1



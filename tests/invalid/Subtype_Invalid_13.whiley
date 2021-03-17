type Point is {int x, int y} where x < y

method test(&Point ptr):
    int x = ptr->x
    // break invariant
    ptr->x = 1
    // restore invariant
    ptr->x = x

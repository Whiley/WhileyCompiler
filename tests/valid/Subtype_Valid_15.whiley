type Point is {int x, int y} where x < y

method mutate(&Point ptr):
    int x = ptr->x
    // preserve invariant
    ptr->x = ptr->y - 1

public export method test():
    &Point p = new {x:1,y:3}
    // Apply mutation
    mutate(p)
    // Check results
    assume (p->x == 2) && (p->y == 3)
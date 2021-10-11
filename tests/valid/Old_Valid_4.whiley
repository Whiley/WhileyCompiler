type Point is {int x, int y}

method m(&Point p)
ensures p->x == old(p->x):
    p->y = p->y + 1

public export method test():
    &Point p = new {x:1,y:2}
    m(p)
    assert p->x == 1

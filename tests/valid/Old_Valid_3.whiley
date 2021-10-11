type Point is {int x, int y}

method m(&Point p)
ensures *p == old(*p):
    skip

public export method test():
    &Point p = new {x:1,y:2}
    m(p)
    assert p->x == 1
    assert p->y == 2

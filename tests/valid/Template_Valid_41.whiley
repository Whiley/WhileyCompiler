type Point is {int x, int y}
type Message<T,S> is { T t, S s }

public export method test():
    Point p = {x:1,y:2}
    Message<Point,bool> h = { t:p, s:false }
    assert h.t.x == 1 && h.t.y == 2
    assert h.s == false

type Point is {int x, int y}
type Message<T,S> is { T t, S s }

public export method test():
    Point p = {x:1,y:2}
    Message<int,Point> h = { t:1, s:p }
    assert h.t == 1
    assert h.s.x == 1 && h.s.y == 2

type Point is {int x, int y}

public export method test():
    &(Point|(int[])) c = new (Point|(int[])) [1,2,3]
    &(Point|(int[])) d = c
    assert (*c) == [1,2,3]
    assert (*d) == [1,2,3]
    *c = {x:1,y:2}
    assert (*c) == {x:1,y:2}
    assert (*d) == {x:1,y:2}


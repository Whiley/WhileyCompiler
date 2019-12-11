type Point is {int x, int y}

public export method test():
    &(Point|(int[])) c = new (Point|(int[])) [1,2,3]
    &(Point|(int[])) d = c
    assume (*c) == [1,2,3]
    assume (*d) == [1,2,3]
    *c = {x:1,y:2}
    assume (*c) == {x:1,y:2}
    assume (*d) == {x:1,y:2}


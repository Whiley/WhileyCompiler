type Point is {int x, int y}

public export method test():
    &(Point|(int[])) c = new (Point|(int[])) [1,2,3]
    &(Point|(int[])) d = c
    *c = [4,5,6]
    assume (*c) == [4,5,6]
    assume (*d) == [4,5,6]
    *c = {x:1,y:2}
    assume (*c) == {x:1,y:2}
    assume (*d) == {x:1,y:2}


type Point is {int x, int y} where x >= 0 && y >= 0

Point p = {x:1,y:2}

public export method test():
    assume p == {x:1,y:2}

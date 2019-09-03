type Point is {int x, int y}

public export method test():
    &?{int x, ...} q = new Point{x:1,y:2}
    // Following not safe
    &Point p = q



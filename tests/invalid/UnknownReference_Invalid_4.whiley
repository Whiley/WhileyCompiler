type Point is {int x, int y}

public export method test():
    &Point p = new Point{x:1,y:2}
    &?{int x, ...} q = p
    // Following not safe ...
    {int x} r = *q



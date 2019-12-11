type Point is {int x, int y}

method main(&{int x, ...} q):
    // Following not safe ...
    {int x} r = *q

public export method test():
    &Point p = new Point{x:1,y:2}
    main(p)



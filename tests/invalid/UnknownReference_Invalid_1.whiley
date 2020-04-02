type Point is {int x, int y}

method main(&{int x, ...} q):
    // Following not safe
    &Point p = q

public export method test():
    main(new Point{x:1,y:2})



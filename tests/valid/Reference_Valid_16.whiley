type Point is {int x, int y}

public export method test():
    &(null|Point) c = new (null|Point) {x:1,y:1}
    &(null|Point) d = c
    *c = {x:2,y:2}
    assume (*c) == {x:2,y:2}
    assume (*d) == {x:2,y:2}
    *c = null
    assume (*c) == null
    assume (*d) == null


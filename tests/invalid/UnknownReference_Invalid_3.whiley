public export method test():
    &?{int x, ...} q = new {x:1,y:2}
    // Following not safe
    &?{int x, int y, ... } p = q



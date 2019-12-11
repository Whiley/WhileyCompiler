method main(&{int x, ...} q):
    // Following not safe
    &{int x, int y, ... } p = q


public export method test():
     main(new {x:1,y:2})



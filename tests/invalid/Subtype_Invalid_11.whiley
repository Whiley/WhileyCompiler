type tup is {int x, int y} where x < y

public export method test():
    tup t = {x:0,y:1}
    //
    t.x = 1
    //
    assume t == {x:1,y:1}

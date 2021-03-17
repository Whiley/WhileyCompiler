type Rec is {int x, int y} where x < y

public export method test():
    Rec r = {x:0,y:1}
    // Increment both by one
    r.x,r.y = r.x+1,r.y+1
    // Check!
    assert r == {x:1,y:2}

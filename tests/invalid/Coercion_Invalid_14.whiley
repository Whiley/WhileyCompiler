type nat is (int x) where x >= 0
type Point is {int x, int y}

public export method test():
    Point p = {x:1,y:1}
    // Force impossible casts
    assert ((Point) {x:1}) == {x:1,y:1}
    assert ((int) {x:1}) == 2
    assert ((int[]) {x:1}) == 2
    assert ((Point) [1]) == {x:1,y:1}
    assert ((int) new 1) == 2
    assert ((int[]) [p]) == 2   
    // Force possible casts
    assert ((nat) 1) == 1
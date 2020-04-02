type nat is (int x) where x >= 0
type Point is {int x, int y}

public export method test():
    // Force impossible casts
    Point p1 = (Point) {x:1}
    int x = (int) {x:1}
    int[] xs = (int[]) {x:1}
    Point p2 = (Point) [1]
    int y = (int) new 1
    int[] ys = (int[]) [p1,p2]
    // Force possible casts
    nat n = (nat) 1

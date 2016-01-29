

type point is {int y, int x}

type listint is int[]

public export method test() :
    listint li = [1, 2, 3]
    point p = {y: 2, x: 1}
    int x = p.x
    assert x == 1
    assert li[0] == 1

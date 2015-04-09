import whiley.lang.*

type point is {int y, int x}

type listint is [int]

type setint is {int}

method main(System.Console sys) -> void:
    setint si = {1, 2, 3}
    listint li = [1, 2, 3]
    point p = {y: 2, x: 1}
    int x = p.x
    assert x == 1
    assert |si| == 3
    assert li[0] == 1

import whiley.lang.*

type point is {int y, int x}

type listint is [int]

type setint is {int}

method main(System.Console sys) -> void:
    setint si = {1, 2, 3}
    listint li = [1, 2, 3]
    point p = {y: 2, x: 1}
    int x = p.x
    sys.out.println(x)
    sys.out.println(|si|)
    sys.out.println(li[0])

import whiley.lang.System

type point is {int y, int x}

type listint is [int]

type setint is {int}

method main(System.Console sys) => void:
    setint si = {1, 2, 3}
    listint li = [1, 2, 3]
    point p = {y: 2, x: 1}
    int x = p.x
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(|si|))
    sys.out.println(Any.toString(li[0]))

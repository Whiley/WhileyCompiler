import println from whiley.lang.System

type point is {int y, int x}

type listint is [int]

type setint is {int}

method main(System.Console sys) => void:
    si = {1, 2, 3}
    li = [1, 2, 3]
    p = {y: 2, x: 1}
    x = p.x
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(|si|))
    sys.out.println(Any.toString(li[0]))

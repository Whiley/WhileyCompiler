import whiley.lang.*

type list is [int]

function update(list l, int index, int value) -> list:
    l[index] = value
    return l

method main(System.Console sys) -> void:
    l = ['1', '2', '3']
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))
    l = "Hello World"
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))

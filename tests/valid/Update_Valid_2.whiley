import whiley.lang.System

type liststr is [char] | string

function update(liststr l, int index, char value) => liststr:
    l[index] = value
    return l

method main(System.Console sys) => void:
    l = ['1', '2', '3']
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))
    l = "Hello World"
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))

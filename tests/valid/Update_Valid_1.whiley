import whiley.lang.*

type listdict is [int] | {int=>int}

function update(listdict l, int index, int value) -> listdict:
    l[index] = value
    return l

method main(System.Console sys) -> void:
    l = [1, 2, 3]
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))
    l = {1=>1, 2=>2, 3=>3}
    sys.out.println(update(l, 1, 0))
    sys.out.println(update(l, 2, 0))

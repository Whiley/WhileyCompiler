import whiley.lang.*

type Link is {LinkedList next, int data}

type LinkedList is null | Link

function sum_1(LinkedList ls) -> int:
    if ls is null:
        return 0
    else:
        return ls.data + sum_1(ls.next)

function sum_2(LinkedList ls) -> int:
    if ls == null:
        return 0
    else:
        return ls.data + sum_2(ls.next)

function sum_3(LinkedList ls) -> int:
    if ls != null:
        return ls.data + sum_3(ls.next)
    else:
        return 0

method main(System.Console sys) -> void:
    LinkedList ls = {next: null, data: 1}
    ls = {next: ls, data: 2}
    ls = {next: ls, data: 3}
    sys.out.println(sum_1(ls))
    sys.out.println(sum_2(ls))
    sys.out.println(sum_3(ls))

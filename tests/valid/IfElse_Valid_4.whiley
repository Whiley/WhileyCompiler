

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

public export method test() :
    LinkedList ls = {next: null, data: 1}
    ls = {next: ls, data: 2}
    ls = {next: ls, data: 3}
    assume sum_1(ls) == 6
    assume sum_2(ls) == 6
    assume sum_3(ls) == 6

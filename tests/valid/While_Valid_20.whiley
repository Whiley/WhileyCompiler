

type LinkedList is null | {LinkedList next, int data}

function sum(LinkedList l) -> int:
    int r = 0
    while !(l is null):
        r = r + l.data
        l = l.next
    return r

public export method test() :
    LinkedList list = null
    assume sum(list) == 0
    list = {next: list, data: 1}
    assume sum(list) == 1
    list = {next: list, data: 2324}
    assume sum(list) == 2325
    list = {next: list, data: 2}
    assume sum(list) == 2327

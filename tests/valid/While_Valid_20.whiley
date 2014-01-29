import whiley.lang.System

type LinkedList is null | {LinkedList next, int data}

function sum(LinkedList l) => int:
    int r = 0
    while !(l is null):
        r = r + l.data
        l = l.next
    return r

method main(System.Console sys) => void:
    LinkedList list = null
    sys.out.println("SUM: " ++ sum(list))
    list = {next: list, data: 1}
    sys.out.println("SUM: " ++ sum(list))
    list = {next: list, data: 2324}
    sys.out.println("SUM: " ++ sum(list))
    list = {next: list, data: 2}
    sys.out.println("SUM: " ++ sum(list))

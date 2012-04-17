import println from whiley.lang.System

define LinkedList as null | {int data, LinkedList next}

int sum(LinkedList l):
    r = 0
    while !(l is null):
        r = r + l.data
        l = l.next
    return r

void ::main(System.Console sys):
    list = null
    sys.out.println("SUM: " + sum(list))
    list = {data: 1, next: list}
    sys.out.println("SUM: " + sum(list))
    list = {data: 2324, next: list}
    sys.out.println("SUM: " + sum(list))
    list = {data: 2, next: list}
    sys.out.println("SUM: " + sum(list))

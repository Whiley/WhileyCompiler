import whiley.lang.System

type Link is {LinkedList next, int data}

type LinkedList is null | Link

function sum(LinkedList l) => int:
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

method main(System.Console sys) => void:
    l1 = {next: null, data: 1}
    l2 = {next: l1, data: 2}
    l3 = {next: l2, data: 3}
    sys.out.println(Any.toString(sum(l1)))
    sys.out.println(Any.toString(sum(l2)))
    sys.out.println(Any.toString(sum(l3)))

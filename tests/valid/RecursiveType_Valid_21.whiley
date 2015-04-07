import whiley.lang.*

type Link is {LinkedList next, int data}

type LinkedList is null | Link

function sum(LinkedList l) -> int:
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

method main(System.Console sys) -> void:
    LinkedList l1 = {next: null, data: 1}
    LinkedList l2 = {next: l1, data: 2}
    LinkedList l3 = {next: l2, data: 3}
    sys.out.println(sum(l1))
    sys.out.println(sum(l2))
    sys.out.println(sum(l3))

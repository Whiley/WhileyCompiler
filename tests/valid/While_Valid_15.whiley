import whiley.lang.*

type Leaf is int

type Link is {LinkedList next}

type LinkedList is Leaf | Link

function dist(LinkedList list) -> Leaf:
    int distance = 0
    while list is Link:
        list = list.next
        distance = distance + 1
    return list + distance

method main(System.Console sys) -> void:
    LinkedList list = 123
    list = {next: list}
    list = {next: list}
    sys.out.println_s("DISTANCE: " ++ Any.toString(dist(list)))
    list = {next: list}
    list = {next: list}
    sys.out.println_s("DISTANCE: " ++ Any.toString(dist(list)))

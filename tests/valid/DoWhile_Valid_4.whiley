import whiley.lang.System

type Leaf is int

type Link is {LinkedList next}

type LinkedList is Leaf | Link

function dist(Link list) => Leaf:
    int distance = 0
    do:
        list = list.next
        distance = distance + 1
    while list is Link
    //
    return list + distance

method main(System.Console sys) => void:
    Link list = 123
    list = {next: list}
    list = {next: list}
    sys.out.println("DISTANCE: " ++ dist(list))
    list = {next: list}
    list = {next: list}
    sys.out.println("DISTANCE: " ++ dist(list))

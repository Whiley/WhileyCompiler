

type Leaf is int

type Link is {LinkedList next}

type LinkedList is Leaf | Link

function dist(LinkedList list) -> Leaf:
    int distance = 0
    while list is Link:
        list = list.next
        distance = distance + 1
    return list + distance

public export method test() :
    LinkedList list = 123
    list = {next: list}
    list = {next: list}
    assume dist(list) == 125
    list = {next: list}
    list = {next: list}
    assume dist(list) == 127

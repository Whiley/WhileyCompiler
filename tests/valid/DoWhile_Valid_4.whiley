type Leaf is int
type Link is {LinkedList next}
type LinkedList is Leaf | Link

function dist(Link list) -> Leaf:
    LinkedList iter = list
    int distance = 0
    // Following shouldn't be necessary!
    assert iter is Link
    do:
        iter = iter.next
        distance = distance + 1
    while iter is Link
    //
    return iter + distance

public export method test() :
    Link list = {next: 123}
    list = {next: list}
    assume dist(list) == 125
    list = {next: list}
    list = {next: list}
    assume dist(list) == 127

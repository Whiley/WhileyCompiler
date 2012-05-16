import println from whiley.lang.System

define Leaf as int
define Link as { LinkedList next }
define LinkedList as Leaf | Link

Leaf dist(Link list):
    distance = 0
    do:
        // list must be a Link
        list = list.next
        distance = distance + 1
    while list is Link
    // list must be a Leaf
    return list + distance

void ::main(System.Console sys):
    list = 123
    list = { next: list }
    list = { next: list }
    sys.out.println("DISTANCE: " + dist(list))
    list = { next: list }
    list = { next: list }
    sys.out.println("DISTANCE: " + dist(list))

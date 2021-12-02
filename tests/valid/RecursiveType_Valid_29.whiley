type nat is (int x) where x >= 0
type Link is { LinkedList next, int data }
type LinkedList is null | Link

property isLength(LinkedList list, nat len) -> (bool r):
    if list is null:
        return len == 0
    else:
        return isLength(list.next,len-1)

function length(LinkedList list) -> (nat r)
ensures isLength(list,r):
    if list is null:
        return 0
    else:
        return 1 + length(list.next)

public export method test():
    LinkedList l0 = null
    LinkedList l1 = {next: l0, data: 0}
    LinkedList l2 = {next: l1, data: 0}
    LinkedList l3 = {next: l2, data: 0}
    //
    assume length(l0) == 0
    assume length(l1) == 1
    assume length(l2) == 2
    assume length(l3) == 3    
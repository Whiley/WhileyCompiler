type nat is (int x) where x >= 0
type Link is { LinkedList next, int data }
type LinkedList is null | Link

property isLength(LinkedList list, nat len)
where (list is null) ==> (len == 0)
where (list is Link) ==> (len == 1)

function length(LinkedList list) -> (nat r)
ensures isLength(list,r):
    if list is null:
        return 0
    else:
        return 1 + length(list.next)
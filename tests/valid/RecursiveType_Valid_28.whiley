type Link is {
    int data,
    LinkedList next
}

type LinkedList is null | Link

function length(LinkedList ls) -> (int r)
// Return is non-negative
ensures r >= 0
// Return is positive then ls non-empty
ensures r > 0 <==> ls is Link:
    //
    if ls is null:
        return 0
    else:
        return 1 + length(ls.next)

function get(Link ls, int i) -> int
// Index i is within bounds
requires i >= 0 && i < length(ls):
    //
    if i == 0:
        return ls.data
    else:
        assert ls.next is Link
        return get(ls.next,i-1)

public export method test():
    LinkedList l1 = null
    Link l2 = { data: 1, next: l1 }
    Link l3 = { data: 2, next: l2 }
    //
    assume length(l1) == 0
    assume length(l2) == 1
    assume length(l3) == 2
    //
    assume get(l2,0) == 1
    assume get(l3,0) == 2
    assume get(l3,1) == 1

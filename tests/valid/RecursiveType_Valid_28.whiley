type Link is {
    int data,
    LinkedList next
}

type LinkedList is null | Link

// index i is "within bounds" of ls
property within(Link ls, int i) -> (bool r):
    // posiive index must be recurisvely satisfied    
    (i > 0 && ls.next is Link && within(ls.next,i-1)) ||
    // index within bounds
    i == 0

function get(Link ls, int i) -> int
// Index i is within bounds
requires within(ls,i):
    //
    if i == 0:
        return ls.data
    else:
        // Following assertion required retyping ls
        assert ls.next is Link
        //
        return get(ls.next,i-1)

public export method test():
    LinkedList l1 = null
    Link l2 = { data: 1, next: l1 }
    Link l3 = { data: 2, next: l2 }
    //
    assume get(l2,0) == 1
    assume get(l3,0) == 2
    assume get(l3,1) == 1

// This benchmark was created specifically to test recursive
// properties.
type Node is {int data, List next}
type List is null|Node

// A recursive property capturing the concept of
// the length of a List
property length(List l, int len)
where l is null ==> len == 0
where l is Node ==> length(l.next, len-1)

// Another recursive property capturing the different in length
// between the head of a list and a given position within that
// list.
property diff(List head, List pos, int d)
where (head == pos) ==> (d == 0)
where (head is Node) ==> diff(head.next, pos, d-1)

function len(List l) -> (int r)
// Ensure we capture the real length
ensures length(l,r):
    //
    int x = 0
    List ol = l // ghost
    // iterate until 
    while l is Node
    where diff(ol,l,x):
        l = l.next
        x = x + 1
    //
    return x

public export method test():
    // List of length 1
    List l1 = {data: 0, next: null}
    // List of length 2
    List l2 = {data: 0, next: l1}
    // List of length 3
    List l3 = {data: 0, next: l2}
    //
    assert len(null) == 0
    //
    assert len(l1) == 1
    //
    assert len(l2) == 2
    //
    assert len(l3) == 3    
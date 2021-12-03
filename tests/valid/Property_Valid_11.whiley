// This benchmark was created specifically to test recursive
// properties.
type Node is {int data, List next}
type List is null|Node

// A recursive property capturing the concept of
// the length of a List
property sum(List l, int s) -> (bool r):
    (l is Node && sum(l.next, s - l.data)) ||
    (l is null && s == 0)

public export method test():
    // List of length 1
    List l1 = {data: 0, next: null}
    // List of length 2
    List l2 = {data: 1, next: l1}
    // List of length 3
    List l3 = {data: 5, next: l2}
    //
    assert sum(null,0)
    //
    assert sum(l1,0)
    //
    assert sum(l2,1)
    //
    assert sum(l3,6)
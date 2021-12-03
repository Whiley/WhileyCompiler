// This benchmark was created specifically to test recursive
// properties.
type Node is {int data, List next}
type List is null|Node

// A recursive property capturing the concept of
// the length of a List
property length(List l, int len) -> (bool r):
    (l is null && len == 0) ||
    (l is Node && length(l.next, len-1))

public export method test():
    // List of length 1
    List l1 = {data: 0, next: null}
    // List of length 2
    List l2 = {data: 0, next: l1}
    // List of length 3
    List l3 = {data: 0, next: l2}
    //
    assert length(null,0)
    //
    assert length(l1,1)
    //
    assert length(l2,2)
    //
    assert length(l3,3)
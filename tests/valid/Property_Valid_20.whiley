// This benchmark was created specifically to test recursive
// properties.
type Node is {List next}
type List is null|Node

// A given (sub)list is "reachable" from its enclosing list
property reachable(List head, Node child) -> (bool r):
    // Unroll in backwards direction
    return (head == child) || reachable(head,{next: child})

function len(List l) -> (int r):
    //
    int x = 0
    List ol = l // ghost
    // iterate until 
    while l is Node
    where l is null || reachable(ol,l):
        l = l.next
        x = x + 1
    //
    return x

public export method test():
    // List of length 1
    List l1 = {next: null}
    // List of length 2
    List l2 = {next: l1}
    // List of length 3
    List l3 = {next: l2}
    //
    assume len(null) == 0
    //
    assume len(l1) == 1
    //
    assume len(l2) == 2
    //
    assume len(l3) == 3

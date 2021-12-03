type List is null|Node
type Node is { List next }

property len(List l, int n) -> (bool r):
    (l is null && n == 0) ||
    (l is Node && n > 0 && len(l.next, n-1))

function nop(List l, int n) -> (List r)
requires len(l,n)
ensures len(r,n):
    return l

public export method test():
    // List of length 0
    List l0 = null
    // List of length 1
    List l1 = {next: l0}
    // List of length 2
    List l2 = {next: l1}
    // List of length 3
    List l3 = {next: l2}
    //
    assert nop(l0,0) == l0
    //
    assert nop(l1,1) == l1
    //
    assert nop(l2,2) == l2

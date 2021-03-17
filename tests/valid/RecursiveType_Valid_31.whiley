type Node is { int data, List next }
type List is null | Node

function cons(int item, List list) -> (List r)
ensures r is Node && r.next == list && r.data == item:
    return { data: item, next: list }

public export method test():
    List l = null
    //
    l = cons(1,l)
    assert l is Node && l.data == 1
    assert l is Node && l.next is null    
    //
    l = cons(2,l)
    assert l is Node && l.data == 2
    assert l is Node && l.next is Node && l.next.next is null

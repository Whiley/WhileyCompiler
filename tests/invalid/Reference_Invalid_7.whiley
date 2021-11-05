type LinkedList is null | &{ int data, LinkedList next }

property acyclic(LinkedList l)
where l is null || acyclic(l->next)

method nop(LinkedList l)
requires acyclic(l):
    //
    skip

public export method test():
    LinkedList l1 = new { data: 1, next: null }
    LinkedList l2 = new { data: 2, next: l1 }
    // create cyclic list
    l1->next = l2
    //
    nop(l2)

type LinkedList is null | { int data, LinkedList next }

function sum(LinkedList l) -> (int r):
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

type Reducer is function(LinkedList)->(int)

function apply(Reducer r, LinkedList l) -> int:
    return r(l)

public export method test():
    LinkedList l1 = null
    LinkedList l2 = {data: 1, next: l1}
    LinkedList l3 = {data: -1, next: l2}
    LinkedList l4 = {data: 10, next: l3}
    LinkedList l5 = {data: 3, next: l4}
    //
    assume apply(&sum,l1) == 0
    //
    assume apply(&sum,l2) == 1
    //
    assume apply(&sum,l3) == 0
    //
    assume apply(&sum,l4) == 10
    //
    assume apply(&sum,l5) == 13

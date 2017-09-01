type LinkedList is null | { int data, LinkedList next }

function sum(LinkedList l) -> (int r):
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

type Reducer is function(LinkedList)->(int)

function apply(Reducer r, LinkedList l) -> int:
    return r(l)

LinkedList LIST_1 = null
LinkedList LIST_2 = {data: 1, next: LIST_1}
LinkedList LIST_3 = {data: -1, next: LIST_2}
LinkedList LIST_4 = {data: 10, next: LIST_3}
LinkedList LIST_5 = {data: 3, next: LIST_4}

Reducer[] FUNCTIONS = [ &sum ]

public export method test():
    int i = 0
    while i < |FUNCTIONS| where i >= 0:
        //
        assume apply(FUNCTIONS[i],LIST_1) == 0
        //
        assume apply(FUNCTIONS[i],LIST_2) == 1
        //
        assume apply(FUNCTIONS[i],LIST_3) == 0
        //
        assume apply(FUNCTIONS[i],LIST_4) == 10
        //
        assume apply(FUNCTIONS[i],LIST_5) == 13
        // 
        i = i + 1
    //

type LinkedList is null | { int data, LinkedList next }

function sum(LinkedList l) -> (int r):
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

type Reducer is function(LinkedList)->(int)

function apply(Reducer r, LinkedList l) -> int:
    return r(l)

constant LIST_1 is null
constant LIST_2 is {data: 1, next: LIST_1}
constant LIST_3 is {data: -1, next: LIST_2}
constant LIST_4 is {data: 10, next: LIST_3}
constant LIST_5 is {data: 3, next: LIST_4}

constant FUNCTIONS is [ &sum ]

public export method test():
    int i = 0
    while i < |FUNCTIONS|:
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

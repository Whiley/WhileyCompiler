
type nat is (int n) where n >= 0

type LinkedList is int | {LinkedList next, int data}

type posLink is {posList next, nat data}

type posList is int | posLink

function sum(LinkedList list) -> nat:
    if list is int:
        return 0
    else:
        return list.data + sum(list.next)

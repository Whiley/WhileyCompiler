type LinkedList is null | { LinkedList next, int data }

function length(LinkedList list, int n) -> LinkedList:
    return list.next
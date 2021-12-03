type LinkedList is null | { LinkedList next, int data }

property length(LinkedList list, int n) -> (bool r):
    return length(list.next,n-1)

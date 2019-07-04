type LinkedList is null | { LinkedList next, int data }

method length(LinkedList list, int n):
    length(list.next,n-1)
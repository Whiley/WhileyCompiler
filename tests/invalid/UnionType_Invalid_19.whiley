type LinkedList is null | { LinkedList next, int data }

property length(LinkedList list, int n)
where length(list.next,n-1)

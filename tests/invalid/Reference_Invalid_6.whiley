public type LinkedList is null | &{ int data, LinkedList next }

public method append(LinkedList head, LinkedList tail) -> LinkedList:
    if head is null:
        return tail
    else if tail is null:
        return head
    else:
        // quickcheck should catch this
        assert head != tail
        //
        return new { data: head->data, next: append(head->next,tail) }

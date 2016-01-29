

type SortedList is null | SortedListNode

type SortedListNode is ({SortedList next, int data} this)
where (this.next == null) || (this.data < this.next.data)

function SortedList(int head, SortedList tail) -> SortedList
requires (tail == null) || (head < tail.data):
    return {next: tail, data: head}

function contains(int item, SortedList list) -> bool:
    if list == null:
        return false
    else:
        if list.data == item:
            return true
        else:
            if list.data > item:
                return false
            else:
                return contains(item, list.next)

public export method test() :
    SortedList list = SortedList(10, null)
    list = SortedList(5, list)
    list = SortedList(3, list)
    list = SortedList(1, list)
    list = SortedList(0, list)
    assume contains(2, list) == false
    assume contains(3, list) == true
    assume contains(10, list) == true

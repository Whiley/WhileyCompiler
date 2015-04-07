import whiley.lang.*

type SortedList is null | SortedListNode

type SortedListNode is {SortedList next, int data} where (next == null) || (data < next.data)

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

method main(System.Console sys) -> void:
    SortedList list = SortedList(10, null)
    list = SortedList(5, list)
    list = SortedList(3, list)
    list = SortedList(1, list)
    list = SortedList(0, list)
    sys.out.println(contains(2, list))
    sys.out.println(contains(3, list))
    sys.out.println(contains(10, list))

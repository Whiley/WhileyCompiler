type Node is {
    int item,
    List next
} where !(next is null) ==> item < next->item

type List is null | &Node

method test():
    List l1 = null
    List l2 = new { item: 0, next: l1 }
    List l3 = new { item: 1, next: l2 }
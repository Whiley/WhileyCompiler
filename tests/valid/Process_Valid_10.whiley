

type Queue is {[int] items}

method get(&Queue this) -> int:
    int item = this->items[0]
    this->items = this->items[1..|this->items|]
    return item

method put(&Queue this, int item) -> void:
    this->items = this->items ++ [item]

method isEmpty(&Queue this) -> bool:
    return |this->items| == 0

public export method test() -> void:
    [int] items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    &Queue q = new {items: []}
    // Put items into the queue
    put(q, 1)
    assume q->items == [1]
    put(q, 2)
    assume q->items == [1,2]
    put(q, 3)
    assume q->items == [1,2,3]
    put(q, 4)
    assume q->items == [1,2,3,4]
    put(q, 5)
    assume q->items == [1,2,3,4,5]
    // Get items outof the queue
    assume get(q) == 1
    assume !isEmpty(q)
    assume get(q) == 2
    assume !isEmpty(q)
    assume get(q) == 3
    assume !isEmpty(q)
    assume get(q) == 4
    assume !isEmpty(q)
    assume get(q) == 5
    assume isEmpty(q)

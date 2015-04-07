import whiley.lang.*
import whiley.lang.*

type Queue is {[int] items}

method get(&Queue this) -> int:
    int item = this->items[0]
    this->items = this->items[1..|this->items|]
    return item

method put(&Queue this, int item) -> void:
    this->items = this->items ++ [item]

method isEmpty(&Queue this) -> bool:
    return |this->items| == 0

method main(System.Console sys) -> void:
    [int] items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    &Queue q = new {items: []}
    // put items into queue
    for item in items:
        put(q,item)
        sys.out.println_s("PUT: " ++ Any.toString(item))
    // get items out of queue
    while !isEmpty(q):
        sys.out.println_s("GET: " ++ Any.toString(get(q)))

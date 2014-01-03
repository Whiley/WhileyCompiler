import println from whiley.lang.System

type Queue is ref {[int] items}

method get(Queue this) => int:
    item = this->items[0]
    this->items = this->items[1..|this->items|]
    return item

method put(Queue this, int item) => void:
    this->items = this->items + [item]

method isEmpty(Queue this) => bool:
    return |this->items| == 0

method Queue() => Queue:
    return new {items: []}

method main(System.Console sys) => void:
    items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    q = Queue()
    for item in items:
        q.put(item)
        sys.out.println("PUT: " + Any.toString(item))
    while !q.isEmpty():
        sys.out.println("GET: " + Any.toString(q.get()))

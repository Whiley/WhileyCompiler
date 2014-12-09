import * from whiley.lang.*
import * from whiley.lang.*

type EmptyQueue is &{[void] items}

type Queue is &{[int] items}

method get(Queue this) -> int:
    int item = this.items[0]
    this.items = this.items[1..|this.items|]
    return item

method put(Queue this, int item) -> void:
    this.items = this.items ++ [item]

method isEmpty(Queue this) -> bool:
    return |this.items| == 0

method EmptyQueue() -> EmptyQueue:
    return new {items: []}

method broken(EmptyQueue q) -> Queue:
    return q

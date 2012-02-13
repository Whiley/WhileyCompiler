import * from whiley.lang.*
import * from whiley.lang.*

define EmptyQueue as ref { [void] items }
define Queue as ref { [int] items }
	 
int Queue::get():
    item = this.items[0]
    this.items = this.items[1..]
    return item
	 
void Queue::put(int item):
    this.items = this.items + [item]

bool Queue::isEmpty():
    return |this.items| == 0

EmptyQueue ::EmptyQueue():
    return new { items: [] }

Queue ::broken(EmptyQueue q):
    return q // totally broken


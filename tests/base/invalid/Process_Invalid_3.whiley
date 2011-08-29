import whiley.lang.*
import whiley.lang.System:*

define EmptyQueue as process { [void] items }
define Queue as process { [int] items }
	 
int Queue::get():
    item = this.items[0]
    this.items = this.items[1..]
    return item
	 
void Queue::put(int item):
    this.items = this.items + [item]

bool Queue::isEmpty():
    return |this.items| == 0

EmptyQueue ::EmptyQueue():
    return spawn { items: [] }

void ::test():
    eq = EmptyQueue()
    eq.put(1) // invalid

import println from whiley.lang.System

define Queue as ref { [int] items }
	 
int Queue::get():
    item = this->items[0]
    this->items = this->items[1..]
    return item
	 
void Queue::put(int item):
    this->items = this->items + [item]

bool Queue::isEmpty():
    return |this->items| == 0

Queue ::Queue():
    return new { items: [] }

void ::main(System.Console sys):
    items = [1,2,3,4,5,6,7,8,9,10]
    q = Queue()
    // first, push items into queue
    for item in items:
        q.put(item)
        sys.out.println("PUT: " + Any.toString(item))
    // second, retrieve items back from queue
    while !q.isEmpty():
        sys.out.println("GET: " + Any.toString(q.get()))
    

type Queue is {int[] items, int length}

method get(&Queue _this) -> int:
    _this->length = _this->length - 1
    return _this->items[_this->length]

method put(&Queue _this, int item) :
    _this->items[_this->length] = item
    _this->length = _this->length + 1

method isEmpty(&Queue _this) -> bool:
    return _this->length == 0

method Queue(int capacity) -> &Queue:
    int[] slots = [0; capacity]
    //
    return new {items: slots, length: 0}

public export method test() :
    int[] items = [1, 2, 3, 4, 5]
    &Queue q = Queue(5)
    // Put items into the queue    
    put(q, 1)
    assume q->items == [1,0,0,0,0]    
    put(q, 2)
    assume q->items == [1,2,0,0,0]
    put(q, 3)
    assume q->items == [1,2,3,0,0]
    put(q, 4)
    assume q->items == [1,2,3,4,0]    
    put(q, 5)
    assume q->items == [1,2,3,4,5]    
    // Get items outof the queue
    assume get(q) == 5
    assume !isEmpty(q)
    assume get(q) == 4
    assume !isEmpty(q)
    assume get(q) == 3
    assume !isEmpty(q)
    assume get(q) == 2
    assume !isEmpty(q)
    assume get(q) == 1
    assume isEmpty(q)

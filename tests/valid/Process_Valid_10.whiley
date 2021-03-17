type Queue is {int[] items, int length}
where length >= 0 && length <= |items|

method get(&Queue _this) -> int
requires _this->length > 0:
    _this->length = _this->length - 1
    return _this->items[_this->length]

method put(&Queue _this, int item)
requires _this->length < |_this->items|
ensures _this->length > 0:
    _this->items[_this->length] = item
    _this->length = _this->length + 1

method isEmpty(&Queue _this) -> bool:
    return _this->length == 0

// NOTE: following cannot be verified without assumptions because of
// inability to properly specify put and get.
public export method test() :
    int[] items = [1, 2, 3, 4, 5]
    &Queue q = new {items: [0,0,0,0,0], length: 0}
    // Put items into the queue    
    put(q, 1)
    assume q->items == [1,0,0,0,0] && q->length == 1
    put(q, 2)
    assume q->items == [1,2,0,0,0] && q->length == 2
    put(q, 3)
    assume q->items == [1,2,3,0,0] && q->length == 3
    put(q, 4)
    assume q->items == [1,2,3,4,0] && q->length == 4
    put(q, 5)
    assume q->items == [1,2,3,4,5] && q->length == 5
    // Get items outof the queue
    int result = get(q)
    assume result == 5 && q->length == 4
    bool empty = isEmpty(q)
    assume !empty
    //
    result = get(q)
    assume result == 4 && q->length == 3
    empty = isEmpty(q)
    assume !empty
    //
    result = get(q)
    assume result == 3 && q->length == 2
    empty = isEmpty(q)
    assume !empty
    //
    result = get(q)
    assume result == 2 && q->length == 1
    empty = isEmpty(q)
    assume !empty
    //
    result = get(q)
    assume result == 1 && q->length == 0
    empty = isEmpty(q)
    assume empty

// test subtyping (outlives) for method parameters and return types

method <a> m(&a:int x, &a:int y) -> &*:int:
    return new 3

public export method test():
    &this:int ptr1 = this:new 1
    &*:int ptr2 = *:new 2

    &this:int ptr3 = m(ptr1, ptr1)
    assume (*ptr3) == 3
    ptr3 = m<*>(ptr2, ptr2)
    assume (*ptr3) == 3
    ptr3 = m<this>(ptr2, ptr2)
    assume (*ptr3) == 3
    ptr3 = m(ptr1, ptr2)
    assume (*ptr3) == 3
    ptr3 = m(ptr2, ptr1)
    assume (*ptr3) == 3

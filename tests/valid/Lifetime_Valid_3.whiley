// test subtyping (outlives) combined with lifetime parameters

method <a, b> m(&a:int x, &b:int y, &*:int z):
    &a:int ptr1 = x
    ptr1 = a:new 1
    assume (*ptr1) == 1

    &b:int ptr2 = y
    ptr2 = b:new 2
    assume (*ptr2) == 2

    &a:int ptr3 = *:new 3
    assume (*ptr3) == 3

    &b:int ptr4 = *:new 4
    assume (*ptr4) == 4

    &this:int ptr5 = a:new 5
    assume (*ptr5) == 5
    ptr5 = x
    ptr5 = y

    &this:int ptr6 = b:new 6
    assume (*ptr6) == 6
    ptr6 = y
    ptr6 = x

public export method test():
    &this:int ptr1 = this:new 1
    assume (*ptr1) == 1

    m(ptr1, ptr1, new 99)

    myblock:
        &myblock:int ptr2 = myblock:new 2
        assume (*ptr2) == 2

        m(ptr1, ptr2, new 99)
        m(ptr2, ptr1, new 99)
        m(ptr2, ptr2, new 99)

        &myblock:int ptr3 = *:new 3
        assume (*ptr3) == 3

        m(ptr1, ptr3, new 99)
        m(ptr2, ptr3, new 99)
        m(ptr3, ptr1, new 99)
        m(ptr3, ptr2, new 99)
        m(ptr3, ptr3, new 99)

        mynestedblock:
            &mynestedblock:int ptr4 = mynestedblock:new 4
            assume (*ptr4) == 4

            m(ptr1, ptr4, new 99)
            m(ptr2, ptr4, new 99)
            m(ptr3, ptr4, new 99)
            m(ptr4, ptr1, new 99)
            m(ptr4, ptr2, new 99)
            m(ptr4, ptr3, new 99)
            m(ptr4, ptr4, new 99)

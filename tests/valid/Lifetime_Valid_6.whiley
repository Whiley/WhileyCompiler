// test lifetime inference without overloading

method <a, b> m1(&a:int x, &a:int y, &b:int z) -> &a:int:
    return new 3

method <a, b> m2(&a:int x, &b:int y, &b:int z) -> &b:int:
    return new 4

public export method test():
    test:
        &this:int ptr1 = this:new 1
        &test:int ptr2 = test:new 2
        &*:int ptr3 = *:new 2

        // <test, *>
        &test:int ptr4 = m1(ptr1, ptr2, ptr3)

        // <this, test>
        &this:int ptr5 = m1(ptr1, ptr3, ptr2)

        // <*, this>
        &*:int ptr6 = m1(ptr3, ptr3, ptr1)

        // --

        // <this, test>
        &test:int ptr7 = m2(ptr1, ptr2, ptr3)

        // <test, this>
        &this:int ptr8 = m2(ptr1, ptr3, ptr1)

        // <this, *>
        &*:int ptr9 = m2(ptr1, ptr3, ptr3)

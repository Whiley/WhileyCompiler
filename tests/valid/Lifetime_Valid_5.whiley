// test lifetime substitution for method calls

method <a, b> m1(&a:int x, &b:int y) -> &a:int:
    *x = (*x)+1
    &a:int result = a:new (*y)
    return result

method <a, b> m2(&a:int x, &b:int y) -> &b:int:
    *y = (*y)+1
    &b:int result = *:new (*x)
    return result

public export method test():
    test:
        &this:int ptr1 = this:new 1
        &test:int ptr2 = test:new 2

        &this:int ptr3 = m1<this, test>(ptr1, ptr2)
        assume (*ptr1) == 2
        assume (*ptr2) == 2
        assume (*ptr3) == 2

        &test:int ptr4 = m2<this, test>(ptr1, ptr2)
        assume (*ptr1) == 2
        assume (*ptr2) == 3
        assume (*ptr3) == 2
        assume (*ptr4) == 2

        &test:int ptr5 = m1<test, test>(ptr2, ptr2)
        assume (*ptr1) == 2
        assume (*ptr2) == 4
        assume (*ptr3) == 2
        assume (*ptr4) == 2
        assume (*ptr5) == 4

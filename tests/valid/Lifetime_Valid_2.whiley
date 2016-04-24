// test subtyping (outlives) with different lifetimes

public export method test():
    &this:int ptr1 = *:new 1
    assume (*ptr1) == 1

    myblock:
        &myblock:int ptr2 = this:new 2
        assume (*ptr2) == 2

        &myblock:int ptr3 = *:new 3
        assume (*ptr3) == 3

        mynestedblock:
            &mynestedblock:int ptr4 = myblock:new 4
            assume (*ptr4) == 4

            &mynestedblock:int ptr5 = this:new 5
            assume (*ptr5) == 5

            &mynestedblock:int ptr6 = *:new 6
            assume (*ptr6) == 6

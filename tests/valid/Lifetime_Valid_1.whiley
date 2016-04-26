// test different lifetimes without subtyping

public export method test():
    &int ptr1 = new 1
    assume (*ptr1) == 1

    &*:int ptr2 = new 2
    assume (*ptr2) == 2

    &int ptr3 = *:new 3
    assume (*ptr3) == 3

    &this:int ptr4 = this:new 4
    assume (*ptr4) == 4

    myblock:
        &myblock:int ptr5 = myblock:new 5
        assume (*ptr5) == 5

        mynestedblock:
            &mynestedblock:int ptr6 = mynestedblock:new 6
            assume (*ptr6) == 6

            &myblock:int ptr7 = myblock:new 7
            assume (*ptr7) == 7

            &int ptr8 = *:new 8
            assume (*ptr8) == 8

            &this:int ptr9 = this:new 9
            assume (*ptr9) == 9

        &myblock:int ptr10 = myblock:new 10
        assume (*ptr10) == 10

    &*:int ptr11 = *:new 11
    assume (*ptr11) == 11

    // name should be available again
    mynestedblock:
        &mynestedblock:int ptr12 = mynestedblock:new 12
        assume (*ptr12) == 12

    &this:int ptr13 = this:new 13
    assume (*ptr13) == 13

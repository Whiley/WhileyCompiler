// Test that variable declarations are parsed correctly
// (tricky because of headless statements and definite types)

type myint is int

public export method test():
    method()->(&myint) m = &(-> new 1)
    &myint a = m()
    &*:myint b = m()
    &this:myint c = m()
    myblock:
        &myblock:myint d = m()

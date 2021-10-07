function f(&int p) -> int
// old doesn't make sense in this context
ensures *p == old(*p):
    //
    return 0
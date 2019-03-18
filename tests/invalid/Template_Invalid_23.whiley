type Ref<T> is (&T r)
 
public export method test():
    Ref<Int> r1 = new 1
    //
    assert *r1 == 1

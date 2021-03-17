type Ref<T> is &T

method next(Ref<int>[] rs):
    skip

method main():
    Ref<int>[] ps = [new 0, new 1]
    &int q = new 2
    assert *q == 2
    next(ps)
    assert *q == 2
    // Check disjointness
    assert all { i in 0..|ps| | ps[i] != q }

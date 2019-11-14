type Message<T,S> is { T t, S s }

public export method test():
    Message<int,bool> h
    h = { t:1, s:false }
    assert h.t == 1
    assert h.s == false

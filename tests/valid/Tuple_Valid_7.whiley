type handler_t<T> is method(T)

method apply<T>(T k, handler_t<T> h):
    h(k)

method zero(&int p):
    *p = 0

public export method test():
    &int p = new 1
    assert *p == 1
    apply(p,&zero)
    assume *p == 0
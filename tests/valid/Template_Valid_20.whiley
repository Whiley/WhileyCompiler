function project<T>({T f} rec) -> (T r)
ensures rec.f == r:
    return rec.f

public export method test():
    {int f} ri = {f:123}
    {bool f} rb = {f:false}
    {{int g} f} rri = {f:{g:234}}
    // 
    assert project(ri) == 123
    assert project(rb) == false
    assert project(rri) == {g:234}

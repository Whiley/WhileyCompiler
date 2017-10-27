type MyProc1 is &{int data}
type MyProc2 is &{int|bool data}

method set(MyProc2 _this, int|bool d) :
    _this.data = d

method get(MyProc1 _this) -> int:
    return _this.data

method create(int data) -> MyProc1:
    return new {data: data}

public export method test() -> int:
    MyProc2 p = create(1)
    p.set(false)
    return get(p)

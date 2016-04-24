type MyProc1 is &{int data}
type MyProc2 is &{any data}

method set(MyProc2 _this, any d) :
    _this.data = d

method get(MyProc1 _this) -> int:
    return _this.data

method create(int data) -> MyProc1:
    return new {data: data}

method main() -> int:
    MyProc2 p = create(1)
    p.set(false)
    return get(p)

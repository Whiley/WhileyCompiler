type MyProc is &{int data}

method copy(MyProc _this, MyProc p):
    p.data = _this.data

method create(int data) -> MyProc:
    return new {data: data}

method main() :
    MyProc p1 = create(1)
    MyProc p2 = create(2)
    copy(p1,p2)

type MyProc is &{int data}

method copy(MyProc this, MyProc p):
    p.data = this.data

method create(int data) -> MyProc:
    return new {data: data}

method main() -> void:
    MyProc p1 = create(1)
    MyProc p2 = create(2)
    copy(p1,p2)

type MyProc1 is &{int data}
type MyProc2 is &{any data}

method set(MyProc2 this, any d) -> void:
    this.data = d

method get(MyProc1 this) -> int:
    return this.data

method create(int data) -> MyProc1:
    return new {data: data}

method main() -> int:
    MyProc2 p = create(1)
    p.set(1.23)
    return get(p)

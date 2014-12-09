import * from whiley.lang.*

type MyProc is &{int data}

method copy(MyProc this, MyProc p) -> int:
    p.data = this.data

method get(MyProc this) -> int:
    return this.data

method create(System this, int data) -> MyProc:
    return new {data: data}

method main(System.Console sys) -> void:
    p1 = this.create(1)
    p2 = this.create(2)
    p1.copy(p2)
    sys.out.println(Any.toString(p1.get()))
    sys.out.println(Any.toString(p2.get()))

import * from whiley.lang.*

type MyProc1 is &{int data}

type MyProc2 is &{any data}

method set(MyProc2 this, any d) -> void:
    this.data = d

method get(MyProc1 this) -> int:
    return this.data

method create(System this, int data) -> MyProc1:
    return new {data: data}

method main(System.Console sys) -> void:
    p2 = this.create(1)
    p2.set(1.23)
    sys.out.println(Any.toString(p2.get()))

import whiley.lang.*

type Proc is &{int data}

method read(Proc this, int x) -> int:
    return x + this->data

method test(Proc p, int arg) -> int:
    return read(p,arg)

method main(System.Console sys) -> void:
    Proc p = new {data: 1}
    int x = test(p, 123)
    assume x == 124
    x = test(p, 12545)
    assume x == 12546
    x = test(p, -11)
    assume x == -10

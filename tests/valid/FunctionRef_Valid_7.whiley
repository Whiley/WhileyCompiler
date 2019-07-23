

type Proc is &{int data}

method read(Proc _this, int x) -> int:
    return x + _this->data

method get(Proc p, int arg) -> int:
    return read(p,arg)

public export method test() :
    Proc p = new {data: 1}
    int x = get(p, 123)
    assume x == 124
    x = get(p, 12545)
    assume x == 12546
    x = get(p, -11)
    assume x == -10

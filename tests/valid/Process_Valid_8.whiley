type MyProc2 is {int|bool data}

method set(&MyProc2 _this, int d) :
    _this->data = (int|bool)d

method get(&MyProc2 _this) -> int|bool:
    return _this->data

method create(int|bool data) -> &MyProc2:
    return new {data: data}

public export method test() :
    &MyProc2 p2 = create(false)
    set(p2,1)
    int|bool result = get(p2)
    assume result == 1
